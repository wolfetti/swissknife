/*
 * Copyright (C) Fabio Frijo.
 *
 * This file is part of swissknife-common.
 *
 * swissknife-common is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-common is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-common.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.common.business;

import java.io.Serializable;

import org.wolfetti.swissknife.common.utils.ReflectionUtils;

/**
 * Entita' astratta che va estesa da tutti i beans applicativi di base.
 * Implementa l'interfaccia serializable per facilitare l'utilizzo su web
 *
 * @author Fabio Frijo
 */
public abstract class ApplicationEntity implements Serializable {
	private static final long serialVersionUID = 4140937141901051470L;

	@Override
	public String toString(){
		return ReflectionUtils.buildToString(this);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		return super.equals(obj);
	}
}
