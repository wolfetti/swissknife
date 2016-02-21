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
package org.wolfetti.swissknife.db.dao;

import org.wolfetti.swissknife.common.business.ApplicationEntity;

/**
 * Interfaccia che espone tutti i metodi utili per i DAO riferiti
 * alle entit&agrave; con chiave primaria intera
 *
 * @author Fabio Frijo
 */
public interface IdEntityDAO<T extends ApplicationEntity> {
	public T findById(int id);
	public void deleteById(int id);
}
