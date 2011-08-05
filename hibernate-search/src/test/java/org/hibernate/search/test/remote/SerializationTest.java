/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.hibernate.search.test.remote;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.junit.Test;

import org.hibernate.search.backend.AddLuceneWork;
import org.hibernate.search.backend.DeleteLuceneWork;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.OptimizeLuceneWork;
import org.hibernate.search.backend.PurgeAllLuceneWork;
import org.hibernate.search.remote.codex.avro.impl.AvroSerializationProvider;
import org.hibernate.search.remote.codex.impl.LuceneWorkSerializer;
import org.hibernate.search.test.SearchTestCase;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 */
public class SerializationTest extends SearchTestCase {
	@Test
	public void testAvroSerialization() throws Exception {
		LuceneWorkSerializer converter = new LuceneWorkSerializer( new AvroSerializationProvider(), getSearchFactoryImpl() );
		List<LuceneWork> works = new ArrayList<LuceneWork>();
		works.add( new OptimizeLuceneWork() );
		works.add( new OptimizeLuceneWork(RemoteEntity.class) ); //class won't be send over
		works.add( new PurgeAllLuceneWork( RemoteEntity.class ) );
		works.add( new DeleteLuceneWork( 123, "123", RemoteEntity.class ) );
		byte[] bytes = converter.toSerializedModel( works );
		List<LuceneWork> copyOfWorks = converter.toLuceneWorks( bytes );
		assertThat(copyOfWorks).hasSize( works.size() );
		for (int index = 0 ; index < works.size() ; index++) {
			assertLuceneWork( works.get( index ), copyOfWorks.get( index ) );
		}
	}

	private void assertLuceneWork(LuceneWork work, LuceneWork copy) {
		assertThat( copy ).isInstanceOf( work.getClass() );
		if (work instanceof OptimizeLuceneWork) {
			assertOptimize( (OptimizeLuceneWork) work, (OptimizeLuceneWork) copy );
		}
		else if (work instanceof PurgeAllLuceneWork) {
			assertPurgeAll( (PurgeAllLuceneWork) work, (PurgeAllLuceneWork) copy );
		}
		else if (work instanceof DeleteLuceneWork) {
			assertDelete( ( DeleteLuceneWork ) work, ( DeleteLuceneWork ) copy );
		}
		else if (work instanceof AddLuceneWork) {
			assertAdd( ( AddLuceneWork ) work, ( AddLuceneWork ) copy );
		}
	}

	private void assertAdd(AddLuceneWork work, AddLuceneWork copy) {
		assertThat( work.getEntityClass() ).as("Add.getEntityClass is not copied").isEqualTo( copy.getEntityClass() );
		assertThat( work.getId() ).as("Add.getId is not copied").isEqualTo( copy.getId() );
		assertThat( work.getIdInString() ).as("Add.getIdInString is not the same").isEqualTo( copy.getIdInString() );
		assertThat( work.getFieldToAnalyzerMap() ).as("Add.getFieldToAnalyzerMap is not the same").isEqualTo( copy.getFieldToAnalyzerMap() );
		assertDocument( work.getDocument(), copy.getDocument() );
	}

	private void assertDocument(Document document, Document copy) {
		assertThat( document.getBoost() ).isEqualTo( copy.getBoost() );
		for ( int index = 0 ; index < document.getFields().size() ; index++ ) {
			Fieldable field = document.getFields().get(index);
			Fieldable fieldCopy = copy.getFields().get(index);
			assertThat( field ).isInstanceOf( fieldCopy.getClass() );
			if ( field instanceof NumericField ) {
				assertNumericField((NumericField) field, (NumericField) fieldCopy);
			}
			else if ( field instanceof Field ) {
				assertNormalField( ( Field ) field, ( Field ) fieldCopy );
			}
		}

	}

	private void assertNormalField(Field field, Field copy) {
		assertThat( copy.name() ).isEqualTo( field.name() );
		assertThat( copy.getBinaryLength() ).isEqualTo( field.getBinaryLength() );
		assertThat( copy.getBinaryOffset() ).isEqualTo( field.getBinaryOffset() );
		assertThat( copy.getBinaryValue() ).isEqualTo( field.getBinaryValue() );
		assertThat( copy.getBoost() ).isEqualTo( field.getBoost() );
		assertThat( copy.getOmitNorms() ).isEqualTo( field.getOmitNorms() );
		assertThat( copy.getOmitTermFreqAndPositions() ).isEqualTo( field.getOmitTermFreqAndPositions() );
		assertThat( copy.isBinary() ).isEqualTo( field.isBinary() );
		assertThat( copy.isIndexed() ).isEqualTo( field.isIndexed() );
		assertThat( copy.isLazy() ).isEqualTo( field.isLazy() );
		assertThat( copy.isStoreOffsetWithTermVector() ).isEqualTo( field.isStoreOffsetWithTermVector() );
		assertThat( copy.isStorePositionWithTermVector() ).isEqualTo( field.isStorePositionWithTermVector() );
		assertThat( copy.isStored() ).isEqualTo( field.isStored() );
		assertThat( copy.isTokenized() ).isEqualTo( field.isTokenized() );
		assertThat( copy.readerValue() ).isEqualTo( field.readerValue() );
		assertThat( copy.tokenStreamValue() ).isEqualTo( field.tokenStreamValue() );
		assertThat( copy.stringValue() ).isEqualTo( field.stringValue() );

		assertThat( copy.isTermVectorStored() ).isEqualTo( field.isTermVectorStored() );
	}

	private void assertNumericField(NumericField field, NumericField copy) {
		assertThat( copy.name() ).isEqualTo( field.name() );
		assertThat( copy.getBinaryLength() ).isEqualTo( field.getBinaryLength() );
		assertThat( copy.getBinaryOffset() ).isEqualTo( field.getBinaryOffset() );
		assertThat( copy.getBinaryValue() ).isEqualTo( field.getBinaryValue() );
		assertThat( copy.getBoost() ).isEqualTo( field.getBoost() );
		assertThat( copy.getDataType() ).isEqualTo( field.getDataType() );
		assertThat( copy.getNumericValue() ).isEqualTo( field.getNumericValue() );
		assertThat( copy.getOmitNorms() ).isEqualTo( field.getOmitNorms() );
		assertThat( copy.getOmitTermFreqAndPositions() ).isEqualTo( field.getOmitTermFreqAndPositions() );
		assertThat( copy.getPrecisionStep() ).isEqualTo( field.getPrecisionStep() );
		assertThat( copy.isBinary() ).isEqualTo( field.isBinary() );
		assertThat( copy.isIndexed() ).isEqualTo( field.isIndexed() );
		assertThat( copy.isLazy() ).isEqualTo( field.isLazy() );
		assertThat( copy.isStoreOffsetWithTermVector() ).isEqualTo( field.isStoreOffsetWithTermVector() );
		assertThat( copy.isStorePositionWithTermVector() ).isEqualTo( field.isStorePositionWithTermVector() );
		assertThat( copy.isStored() ).isEqualTo( field.isStored() );
		assertThat( copy.isTokenized() ).isEqualTo( field.isTokenized() );
		assertThat( copy.readerValue() ).isEqualTo( field.readerValue() );
		assertThat( copy.tokenStreamValue() ).isEqualTo( field.tokenStreamValue() );
		assertThat( copy.stringValue() ).isEqualTo( field.stringValue() );
	}

	private void assertDelete(DeleteLuceneWork work, DeleteLuceneWork copy) {
		assertThat( work.getEntityClass() ).as("Delete.getEntityClass is not copied").isEqualTo( copy.getEntityClass() );
		assertThat( work.getId() ).as( "Delete.getId is not copied" ).isEqualTo( copy.getId() );
		assertThat( work.getDocument() ).as("Delete.getDocument is not the same").isEqualTo( copy.getDocument() );
		assertThat( work.getIdInString() ).as("Delete.getIdInString is not the same").isEqualTo( copy.getIdInString() );
		assertThat( work.getFieldToAnalyzerMap() ).as( "Delete.getFieldToAnalyzerMap is not the same" ).isEqualTo( copy.getFieldToAnalyzerMap() );
	}

	private void assertOptimize(OptimizeLuceneWork work, OptimizeLuceneWork copy) {
		//nothing besides the type
	}

	private void assertPurgeAll(PurgeAllLuceneWork work, PurgeAllLuceneWork copy) {
		assertThat( work.getEntityClass() ).as("PurgeAll.getEntityClass is not copied").isEqualTo( copy.getEntityClass() );
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				RemoteEntity.class
		};
	}
}