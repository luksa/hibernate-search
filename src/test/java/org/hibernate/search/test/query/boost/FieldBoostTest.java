package org.hibernate.search.test.query.boost;

import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.test.SearchTestCase;

/**
 * @author John Griffin
 */
public class FieldBoostTest extends SearchTestCase {

	public void testBoostedGetDesc() throws Exception {
		FullTextSession fullTextSession = Search.getFullTextSession( openSession() );
		buildBoostedGetIndex( fullTextSession );

		fullTextSession.clear();
		Transaction tx = fullTextSession.beginTransaction();

		QueryParser authorParser = new QueryParser( "author", new StandardAnalyzer() );
		QueryParser descParser = new QueryParser( "description", new StandardAnalyzer() );
		Query author = authorParser.parse( "Wells" );
		Query desc = descParser.parse( "martians" );

		BooleanQuery query = new BooleanQuery();
		query.add( author, BooleanClause.Occur.SHOULD );
		query.add( desc, BooleanClause.Occur.SHOULD );
		//System.out.println( query.toString() );

		org.hibernate.search.FullTextQuery hibQuery =
				fullTextSession.createFullTextQuery( query, BoostedGetDescriptionLibrary.class );
		List results = hibQuery.list();

		//System.out.println( hibQuery.explain( 0 ) );
		//System.out.println( hibQuery.explain( 1 ) );

		assertTrue(
				"incorrect document returned",
				( ( BoostedGetDescriptionLibrary ) results.get( 0 ) ).getDescription().startsWith( "Martians" )
		);

		//cleanup
		for ( Object element : fullTextSession.createQuery( "from " + BoostedGetDescriptionLibrary.class.getName() )
				.list() ) {
			fullTextSession.delete( element );
		}
		tx.commit();
		fullTextSession.close();
	}

	public void testBoostedFieldDesc() throws Exception {
		FullTextSession fullTextSession = Search.getFullTextSession( openSession() );
		buildBoostedFieldIndex( fullTextSession );

		fullTextSession.clear();
		Transaction tx = fullTextSession.beginTransaction();

		QueryParser authorParser = new QueryParser( "author", new StandardAnalyzer() );
		QueryParser descParser = new QueryParser( "description", new StandardAnalyzer() );
		Query author = authorParser.parse( "Wells" );
		Query desc = descParser.parse( "martians" );

		BooleanQuery query = new BooleanQuery();
		query.add( author, BooleanClause.Occur.SHOULD );
		query.add( desc, BooleanClause.Occur.SHOULD );
		//System.out.println( query.toString() );

		org.hibernate.search.FullTextQuery hibQuery =
				fullTextSession.createFullTextQuery( query, BoostedFieldDescriptionLibrary.class );
		List results = hibQuery.list();

		assertTrue(
				"incorrect document boost",
				( ( BoostedFieldDescriptionLibrary ) results.get( 0 ) ).getDescription().startsWith( "Martians" )
		);

		//System.out.println( hibQuery.explain( 0 ) );
		//System.out.println( hibQuery.explain( 1 ) );

		//cleanup
		for ( Object element : fullTextSession.createQuery( "from " + BoostedFieldDescriptionLibrary.class.getName() )
				.list() ) {
			fullTextSession.delete( element );
		}
		tx.commit();
		fullTextSession.close();
	}

	public void testBoostedDesc() throws Exception {
		FullTextSession fullTextSession = Search.getFullTextSession( openSession() );
		buildBoostedDescIndex( fullTextSession );

		fullTextSession.clear();
		Transaction tx = fullTextSession.beginTransaction();

		QueryParser authorParser = new QueryParser( "author", new StandardAnalyzer() );
		QueryParser descParser = new QueryParser( "description", new StandardAnalyzer() );
		Query author = authorParser.parse( "Wells" );
		Query desc = descParser.parse( "martians" );

		BooleanQuery query = new BooleanQuery();
		query.add( author, BooleanClause.Occur.SHOULD );
		query.add( desc, BooleanClause.Occur.SHOULD );
		//System.out.println( query.toString() );

		org.hibernate.search.FullTextQuery hibQuery =
				fullTextSession.createFullTextQuery( query, BoostedDescriptionLibrary.class );
		List results = hibQuery.list();

		//System.out.println( hibQuery.explain( 0 ) );
		//System.out.println( hibQuery.explain( 1 ) );

		assertTrue(
				"incorrect document returned",
				( ( BoostedDescriptionLibrary ) results.get( 0 ) ).getDescription().startsWith( "Martians" )
		);

		//cleanup
		for ( Object element : fullTextSession.createQuery( "from " + BoostedDescriptionLibrary.class.getName() )
				.list() ) {
			fullTextSession.delete( element );
		}
		tx.commit();
		fullTextSession.close();
	}

	private void buildBoostedDescIndex(FullTextSession session) {
		Transaction tx = session.beginTransaction();
		BoostedDescriptionLibrary l = new BoostedDescriptionLibrary();
		l.setAuthor( "H.G. Wells" );
		l.setTitle( "The Invisible Man" );
		l.setDescription( "Scientist discovers invisibility and becomes insane." );
		session.save( l );

		l = new BoostedDescriptionLibrary();
		l.setAuthor( "H.G. Wells" );
		l.setTitle( "War of the Worlds" );
		l.setDescription( "Martians invade earth to eliminate mankind." );
		session.save( l );

		tx.commit();
	}

	private void buildBoostedFieldIndex(FullTextSession session) {
		Transaction tx = session.beginTransaction();
		BoostedFieldDescriptionLibrary l = new BoostedFieldDescriptionLibrary();
		l.setAuthor( "H.G. Wells" );
		l.setTitle( "The Invisible Man" );
		l.setDescription( "Scientist discovers invisibility and becomes insane." );
		session.save( l );

		l = new BoostedFieldDescriptionLibrary();
		l.setAuthor( "H.G. Wells" );
		l.setTitle( "War of the Worlds" );
		l.setDescription( "Martians invade earth to eliminate mankind." );
		session.save( l );

		tx.commit();
	}

	private void buildBoostedGetIndex(FullTextSession session) {
		Transaction tx = session.beginTransaction();
		BoostedGetDescriptionLibrary l = new BoostedGetDescriptionLibrary();
		l.setAuthor( "H.G. Wells" );
		l.setTitle( "The Invisible Man" );
		l.setDescription( "Scientist discovers invisibility and becomes insane." );
		session.save( l );

		l = new BoostedGetDescriptionLibrary();
		l.setAuthor( "H.G. Wells" );
		l.setTitle( "War of the Worlds" );
		l.setDescription( "Martians invade earth to eliminate mankind." );
		session.save( l );

		tx.commit();
	}

	protected Class[] getMappings() {
		return new Class[] {
				BoostedDescriptionLibrary.class,
				BoostedFieldDescriptionLibrary.class,
				BoostedGetDescriptionLibrary.class,
		};
	}
}
