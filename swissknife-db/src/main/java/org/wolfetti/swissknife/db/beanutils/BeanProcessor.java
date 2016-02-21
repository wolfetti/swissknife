/*
 * Copyright(c) 2013 Fabio Frijo.
 *
 * This file is part of swissknife-db.
 *
 * swissknife-db is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-db is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-db.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.db.beanutils;

import java.sql.ResultSet;
import java.util.List;

import org.wolfetti.swissknife.db.exceptions.ConverterException;

public interface BeanProcessor {

	/**
	 * Convert a <code>ResultSet</code> row into a JavaBean.  This
	 * implementation uses reflection and <code>BeanInfo</code> classes to
	 * match column names to bean property names.  Properties are matched to
	 * columns based on several factors:
	 * <br/>
	 * <ol>
	 *     <li>
	 *     The class has a writable property with the same name as a column.
	 *     The name comparison is case insensitive.
	 *     </li>
	 *
	 *     <li>
	 *     The column type can be converted to the property's set method
	 *     parameter type with a ResultSet.get* method.  If the conversion fails
	 *     (ie. the property was an int and the column was a Timestamp) a
	 *     ConverterException is thrown.
	 *     </li>
	 * </ol>
	 *
	 * <p>
	 * Primitive bean properties are set to their defaults when SQL NULL is
	 * returned from the <code>ResultSet</code>.  Numeric fields are set to 0
	 * and booleans are set to false.  Object bean properties are set to
	 * <code>null</code> when SQL NULL is returned.  This is the same behavior
	 * as the <code>ResultSet</code> get* methods.
	 * </p>
	 * @param <T> The type of bean to create
	 * @param rs ResultSet that supplies the bean data
	 * @param type Class from which to create the bean instance
	 * @throws ConverterException if a database access error or other occurs
	 * @return the newly created bean
	 */
	public <T> T toBean(ResultSet rs, Class<T> type)
	throws ConverterException;

	/**
	 * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans.
	 * This implementation uses reflection and <code>BeanInfo</code> classes to
	 * match column names to bean property names. Properties are matched to
	 * columns based on several factors:
	 * <br/>
	 * <ol>
	 *     <li>
	 *     The class has a writable property with the same name as a column.
	 *     The name comparison is case insensitive.
	 *     </li>
	 *
	 *     <li>
	 *     The column type can be converted to the property's set method
	 *     parameter type with a ResultSet.get* method.  If the conversion fails
	 *     (ie. the property was an int and the column was a Timestamp) an
	 *     ConverterException is thrown.
	 *     </li>
	 * </ol>
	 *
	 * <p>
	 * Primitive bean properties are set to their defaults when SQL NULL is
	 * returned from the <code>ResultSet</code>.  Numeric fields are set to 0
	 * and booleans are set to false.  Object bean properties are set to
	 * <code>null</code> when SQL NULL is returned.  This is the same behavior
	 * as the <code>ResultSet</code> get* methods.
	 * </p>
	 * @param <T> The type of bean to create
	 * @param rs ResultSet that supplies the bean data
	 * @param type Class from which to create the bean instance
	 * @throws ConverterException if a database access error or other occurs
	 * @return the newly created List of beans
	 */
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type)
	throws ConverterException;

}