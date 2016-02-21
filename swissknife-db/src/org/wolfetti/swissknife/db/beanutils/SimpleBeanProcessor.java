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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wolfetti.swissknife.db.exceptions.ConverterException;

/**
 * <p>
 * <code>BeanProcessor</code> matches column names to bean property names
 * and converts <code>ResultSet</code> columns into objects for those bean
 * properties.  Subclasses should override the methods in the processing chain
 * to customize behavior.
 * </p>
 *
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @see BasicRowProcessor
 *
 * @since DbUtils 1.1
 */
public class SimpleBeanProcessor implements BeanProcessor {

	/**
	 * The automatically created logger for instance.
	 */

    /**
     * Special array value used by <code>mapColumnsToProperties</code> that
     * indicates there is no bean property that matches a column from a
     * <code>ResultSet</code>.
     */
    protected static final int PROPERTY_NOT_FOUND = -1;

    /**
     * Set a bean's primitive properties to these defaults when SQL NULL
     * is returned.  These are the same as the defaults that ResultSet get*
     * methods return in the event of a NULL column.
     */
    private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();

    /**
     * ResultSet column to bean property name overrides.
     */
    protected final Map<String, String> columnToPropertyOverrides;

    static {
        primitiveDefaults.put(Integer.TYPE, Integer.valueOf(0));
        primitiveDefaults.put(Short.TYPE, Short.valueOf((short) 0));
        primitiveDefaults.put(Byte.TYPE, Byte.valueOf((byte) 0));
        primitiveDefaults.put(Float.TYPE, Float.valueOf(0f));
        primitiveDefaults.put(Double.TYPE, Double.valueOf(0d));
        primitiveDefaults.put(Long.TYPE, Long.valueOf(0L));
        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        primitiveDefaults.put(Character.TYPE, Character.valueOf((char) 0));
    }

    /**
     * Constructor for BeanProcessor.
     */
    public SimpleBeanProcessor() {
        this(new HashMap<String, String>());
    }

    /**
     * Constructor for BeanProcessor configured with column to property name overrides.
     *
     * @param columnToPropertyOverrides ResultSet column to bean property name overrides
     * @since 1.5
     */
    public SimpleBeanProcessor(Map<String, String> columnToPropertyOverrides) {
        super();

        if (columnToPropertyOverrides == null) {
            throw new IllegalArgumentException("columnToPropertyOverrides map cannot be null");
        }

        this.columnToPropertyOverrides = columnToPropertyOverrides;
    }

    /* (non-Javadoc)
	 * @see org.wolfetti.dbconnector.processors.BeanProcessor#toBean(java.sql.ResultSet, java.lang.Class)
	 */
    @Override
	public <T> T toBean(ResultSet rs, Class<T> type)
    throws ConverterException {
    	try {
	    	if (!rs.next()) {
	            return null;
	        }

	        PropertyDescriptor[] props = this.propertyDescriptors(type);

	        ResultSetMetaData rsmd = rs.getMetaData();
	        int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);

	        return this.createBean(rs, type, props, columnToProperty);
	    } catch (SQLException e){
	    	throw new ConverterException("Database access error", e);
	    }
    }

    /* (non-Javadoc)
	 * @see org.wolfetti.dbconnector.processors.BeanProcessor#toBeanList(java.sql.ResultSet, java.lang.Class)
	 */
    @Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type)
    throws ConverterException {
        List<T> results = new ArrayList<T>();

        try {
	        if (!rs.next()) {
	            return results;
	        }

	        PropertyDescriptor[] props = this.propertyDescriptors(type);

	        ResultSetMetaData rsmd = rs.getMetaData();
	        int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);

	        do {
	            results.add(this.createBean(rs, type, props, columnToProperty));
	        }
	        while (rs.next());

        } catch (SQLException e){
        	throw new ConverterException("Database access error", e);
        }

        return results;
    }

    /**
     * Creates a new object and initializes its fields from the ResultSet.
     * @param <T> The type of bean to create
     * @param rs The result set.
     * @param type The bean type (the return type of the object).
     * @param props The property descriptors.
     * @param columnToProperty The column indices in the result set.
     * @return An initialized object.
     * @throws ConverterException if a database error occurs.
     */
    protected <T> T createBean(ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty)
    throws ConverterException {

        T bean = this.newInstance(type);

        for (int i = 1; i < columnToProperty.length; i++) {
            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
                continue;
            }

            PropertyDescriptor prop = props[columnToProperty[i]];
            Class<?> propType = prop.getPropertyType();


            Object value = this.processColumn(rs, i, propType);

            if (propType != null && value == null && propType.isPrimitive()) {
                value = primitiveDefaults.get(propType);
            }

            this.callSetter(bean, prop, value);
        }

        return bean;
    }

    /**
     * Calls the setter method on the target object for the given property.
     * If no setter method exists for the property, this method does nothing.
     * @param target The object to set the property on.
     * @param prop The property to set.
     * @param value The value to pass into the setter.
     * @throws ConverterException if an error occurs setting the property.
     */
    protected void callSetter(Object target, PropertyDescriptor prop, Object value)
    throws ConverterException {
        Method setter = prop.getWriteMethod();

        if (setter == null) {
            return;
        }

        Class<?>[] params = setter.getParameterTypes();
        try {
            // convert types for some popular ones
            if (value instanceof java.util.Date) {
                final String targetType = params[0].getName();
                if ("java.sql.Date".equals(targetType)) {
                    value = new java.sql.Date(((java.util.Date) value).getTime());
                } else
                if ("java.sql.Time".equals(targetType)) {
                    value = new java.sql.Time(((java.util.Date) value).getTime());
                } else
                if ("java.sql.Timestamp".equals(targetType)) {
                    value = new java.sql.Timestamp(((java.util.Date) value).getTime());
                }
            }

            // Don't call setter if the value object isn't the right type
            if (this.isCompatibleType(value, params[0])) {
                setter.invoke(target, new Object[]{value});
            } else {
              throw new ConverterException(
                  "Cannot set " + prop.getName() + ": incompatible types, cannot convert "
                  + value.getClass().getName() + " to " + params[0].getName());
                  // value cannot be null here because isCompatibleType allows null
            }

        } catch (IllegalArgumentException e) {
            throw new ConverterException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new ConverterException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());

        } catch (InvocationTargetException e) {
            throw new ConverterException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());
        }
    }

    /**
     * ResultSet.getObject() returns an Integer object for an INT column.  The
     * setter method for the property might take an Integer or a primitive int.
     * This method returns true if the value can be successfully passed into
     * the setter method.  Remember, Method.invoke() handles the unwrapping
     * of Integer into an int.
     *
     * @param value The value to be passed into the setter method.
     * @param type The setter's parameter type (non-null)
     * @return boolean True if the value is compatible (null => true)
     */
    private boolean isCompatibleType(Object value, Class<?> type) {
        // Do object check first, then primitives
        if (value == null || type.isInstance(value)) {
            return true;

        } else if (type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
            return true;

        } else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
            return true;

        } else if (type.equals(Double.TYPE) && Double.class.isInstance(value)) {
            return true;

        } else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
            return true;

        } else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
            return true;

        } else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
            return true;

        } else if (type.equals(Character.TYPE) && Character.class.isInstance(value)) {
            return true;

        } else if (type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
            return true;

        }
        return false;

    }

    /**
     * Factory method that returns a new instance of the given Class.  This
     * is called at the start of the bean creation process and may be
     * overridden to provide custom behavior like returning a cached bean
     * instance.
     * @param <T> The type of object to create
     * @param c The Class to create an object from.
     * @return A newly created object of the Class.
     * @throws ConverterException if creation failed.
     */
    protected <T> T newInstance(Class<T> c) throws ConverterException {
        try {
            return c.newInstance();

        } catch (InstantiationException e) {
            throw new ConverterException(
                "Cannot create " + c.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new ConverterException(
                "Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Returns a PropertyDescriptor[] for the given Class.
     *
     * @param c The Class to retrieve PropertyDescriptors for.
     * @return A PropertyDescriptor[] describing the Class.
     * @throws ConverterException if introspection failed.
     */
    private PropertyDescriptor[] propertyDescriptors(Class<?> c)
    throws ConverterException {
        // Introspector caches BeanInfo classes for better performance
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);

        } catch (IntrospectionException e) {
            throw new ConverterException("Bean introspection failed: " + e.getMessage());
        }

        return beanInfo.getPropertyDescriptors();
    }

    /**
     * The positions in the returned array represent column numbers.  The
     * values stored at each position represent the index in the
     * <code>PropertyDescriptor[]</code> for the bean property that matches
     * the column name.  If no bean property was found for a column, the
     * position is set to <code>PROPERTY_NOT_FOUND</code>.
     *
     * @param rsmd The <code>ResultSetMetaData</code> containing column
     * information.
     *
     * @param props The bean property descriptors.
     *
     * @throws ConverterException if a database access error occurs
     *
     * @return An int[] with column index to property index mappings.  The 0th
     * element is meaningless because JDBC column indexing starts at 1.
     */
    protected int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props)
    throws ConverterException {
    	try {
	        int cols = rsmd.getColumnCount();
	        int[] columnToProperty = new int[cols + 1];
	        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

	        for (int col = 1; col <= cols; col++) {
	            String columnName = rsmd.getColumnLabel(col);
	            if (null == columnName || 0 == columnName.length()) {
	              columnName = rsmd.getColumnName(col);
	            }
	            String propertyName = columnToPropertyOverrides.get(columnName);
	            if (propertyName == null) {
	                propertyName = columnName;
	            }
	            for (int i = 0; i < props.length; i++) {

	                if (propertyName.equalsIgnoreCase(props[i].getName())) {
	                    columnToProperty[col] = i;
	                    break;
	                }
	            }
	        }

	        return columnToProperty;
	    } catch (SQLException e){
	    	throw new ConverterException("Database access error", e);
	    }
    }

    /**
     * Convert a <code>ResultSet</code> column into an object.  Simple
     * implementations could just call <code>rs.getObject(index)</code> while
     * more complex implementations could perform type manipulation to match
     * the column's type to the bean property type.
     *
     * <p>
     * This implementation calls the appropriate <code>ResultSet</code> getter
     * method for the given property type to perform the type conversion.  If
     * the property type doesn't match one of the supported
     * <code>ResultSet</code> types, <code>getObject</code> is called.
     * </p>
     *
     * @param rs The <code>ResultSet</code> currently being processed.  It is
     * positioned on a valid row before being passed into this method.
     *
     * @param index The current column index being processed.
     *
     * @param propType The bean property type that this column needs to be
     * converted into.
     *
     * @throws ConverterException if a database access error occurs
     *
     * @return The object from the <code>ResultSet</code> at the given column
     * index after optional type processing or <code>null</code> if the column
     * value was SQL NULL.
     */
    protected Object processColumn(ResultSet rs, int index, Class<?> propType)
    throws ConverterException {

    	try {
	        if ( !propType.isPrimitive() && rs.getObject(index) == null ) {
	            return null;
	        }

	        // String
	        if (propType.equals(String.class)) {
	            return rs.getString(index);

	        }

	        // int
	        else if (
	            propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
	            return Integer.valueOf(rs.getInt(index));

	        }

	        // boolean
	        else if (
	            propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
	            return Boolean.valueOf(rs.getBoolean(index));

	        }

	        // long
	        else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
	            return Long.valueOf(rs.getLong(index));

	        }

	        // double
	        else if (
	            propType.equals(Double.TYPE) || propType.equals(Double.class)) {
	            return Double.valueOf(rs.getDouble(index));

	        }

	        // float
	        else if (
	            propType.equals(Float.TYPE) || propType.equals(Float.class)) {
	            return Float.valueOf(rs.getFloat(index));

	        }

	        // short
	        else if (
	            propType.equals(Short.TYPE) || propType.equals(Short.class)) {
	            return Short.valueOf(rs.getShort(index));

	        }

	        // byte
	        else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
	            return Byte.valueOf(rs.getByte(index));

	        }

	        // java.sql.Timestamp
	        else if (propType.equals(Timestamp.class)) {
	            return rs.getTimestamp(index);

	        }

	        // java.sql.SQLXML
	        else if (propType.equals(SQLXML.class)) {
	            return rs.getSQLXML(index);

	        }

	        // Generic object item
	        else {
	            return rs.getObject(index);
	        }
        } catch (SQLException e){
        	throw new ConverterException("Database access error", e);
        }
    }
}
