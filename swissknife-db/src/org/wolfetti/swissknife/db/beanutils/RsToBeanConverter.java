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
import java.sql.SQLException;
import java.util.List;

import org.wolfetti.swissknife.db.exceptions.ConverterException;

/**
 * Classe che effettua la conversione di un <code>ResultSet</code> in un oggetto o una lista di oggetti.
 *
 * @author Fabio Frijo
 *
 * @param <T> Il tipo dell'oggetto che deve essere restituito dopo la conversione
 */
public class RsToBeanConverter<T> implements ResultSetConverter {

    /**
     * The type class.
     */
    private final Class<T> type;

    /**
     * The type class.
     */
    private BeanProcessor processor;

    /**
     * Costruisce il converter corretto per il tipo scelto.
     * @param type The bean type (the return type of the object).
     * @param processor The specific {@link BeanProcessor} istance.
     */
	public RsToBeanConverter(Class<T> type, BeanProcessor processor){
    	this.type = type;
    	this.processor = processor;
    }

    /**
     * Costruisce il converter corretto per il tipo scelto.
     * @param type The bean type (the return type of the object).
     */
	public RsToBeanConverter(Class<T> type){
    	this(type, new SimpleBeanProcessor());
    }

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
     *     (ie. the property was an int and the column was a Timestamp) an
     *     SQLException is thrown.
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
     * @param rs ResultSet that supplies the bean data
     * @param type The bean type (the return type of the object).
     * @throws ConverterException if a database access error occurs
     * @return the newly created bean
     */
	public static <T> T getSingle(ResultSet rs, Class<T> type)
	throws ConverterException {
		return new RsToBeanConverter<T>(type).getSingleItem(rs);
	}

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
     *     SQLException is thrown.
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
     * @param rs ResultSet that supplies the bean data
     * @param type The bean type (the return type of the object).
     * @throws SQLException if a database access error occurs
     * @return the newly created List of beans
     */
	public static <T> List<T> getList(ResultSet rs, Class<T> type)
	throws ConverterException {
		return new RsToBeanConverter<T>(type).getItemsList(rs);
	}

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
     *     (ie. the property was an int and the column was a Timestamp) an
     *     SQLException is thrown.
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
     * @param rs ResultSet that supplies the bean data
     * @throws ConverterException if a database access error occurs
     * @return the newly created bean
     */
	@Override
	public T getSingleItem(ResultSet rs)
	throws ConverterException {
		return this.processor.toBean(rs, this.type);
	}

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
     *     SQLException is thrown.
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
     * @param rs ResultSet that supplies the bean data
     * @throws SQLException if a database access error occurs
     * @throws ConverterException if an error occours during object conversion
     * @return the newly created List of beans
     */
	@Override
	public List<T> getItemsList(ResultSet rs)
	throws ConverterException {
		return this.processor.toBeanList(rs, this.type);
	}
}
