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
package org.wolfetti.swissknife.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.wolfetti.swissknife.common.utils.exceptions.EncryptionException;

/**
 * Classe che esegue i crittaggi dellle password, per adesso i metodi
 * implementati sono MD5 e SHA-1
 *
 * @author Fabio Frijo
 */
public class PasswordUtils {
	private PasswordUtils(){}

	/**
	 * Cripta la stringa in SHA-1 e la trasforma in esadecimale.
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static String sha1Encrypt(String input) {
		return sha1Encrypt(input, "");
	}

	/**
	 * Cripta la stringa in SHA-1 e la trasforma in esadecimale.
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static String md5Encrypt(String input) {
		return md5Encrypt(input, "");
	}

	/**
	 * Cripta la stringa in SHA-1 e la trasforma in esadecimale.
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static String sha1Encrypt(String input, String salt) {
		return encrypt("SHA-1", input + salt);
	}

	/**
	 * Cripta la stringa in SHA-1 e la trasforma in esadecimale.
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static String md5Encrypt(String input, String salt) {
		return encrypt("MD5", input + salt);
	}

	/**
	 * Restituisce un bytearray nel digest indicato.
	 *
	 * @param method
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private static byte[] getEncryptByteArray(String method, String input) {
		MessageDigest d;
		
		try {
			d = MessageDigest.getInstance(method);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException("No such method: " + method, e);
		}
		
		d.reset();
		d.update(input.getBytes());
		
		return d.digest();
	}

	/**
	 * Trasforma una stringa in una stringa criptata esadecimale.
	 */
	private static String encrypt(String method, String input) {
		byte[] byteArray = getEncryptByteArray(method, input);

		StringBuffer sb = new StringBuffer(byteArray.length * 2);

		for (int i = 0; i < byteArray.length; i++){
			int v = byteArray[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}

			sb.append(Integer.toHexString(v));
		}

		String response = sb.toString().toUpperCase();

		return response;
	}
}
