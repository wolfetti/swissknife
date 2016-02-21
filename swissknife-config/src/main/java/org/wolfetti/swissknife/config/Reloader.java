/*
 * Copyright(c) 2013 Fabio Frijo.
 *
 * This file is part of swissknife-config.
 *
 * swissknife-config is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-config is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-config.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.config;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration.Configuration;
import org.wolfetti.swissknife.common.log.Log;
import org.wolfetti.swissknife.common.log.LogFactory;
import org.wolfetti.swissknife.config.exceptions.ReloadFault;

/**
 * This class is a skel for creation of different reloading strategies when
 * the commons-configuration library does not implement that and manage the
 * timer who schedule the reload operation.
 * <br>
 * Extensions of this class must be writed as subclasses of {@link ConfigFactory},
 * for easy reload of the single static istance of {@link Configuration} used.
 *
 * @author Fabio Frijo
 */
abstract class Reloader extends TimerTask {

	/**
	 * Class logger.
	 */
	private static final Log log = LogFactory.getLog(Reloader.class);

	/**
	 * Variable for timer status check
	 */
	private static boolean running = false;

    /**
     * Constant for the default refresh delay.
     */
    private static final int DEFAULT_REFRESH_DELAY = 5000;

    /**
     * The timer that will schedule the reloading process
     */
    private static final Timer timer = new Timer();

    /**
     * Configuration reload method.
	 *
	 * @throws ReloadFault
	 * 	When configuration is <code>null</code>
     */
	abstract void reload() throws ReloadFault;

	/**
	 * Start a new timer with the {@link Reloader} implementation.
	 * @param reloader
	 */
	static void start(Reloader reloader){
		if(!running){
			timer.schedule(reloader, DEFAULT_REFRESH_DELAY, DEFAULT_REFRESH_DELAY);
			running = true;
		} else {
			log.warn("Configuration reloading is already started!");
		}
	}

	/**
	 * Stop the running {@link Reloader} implementation.
	 */
	static void stop(){
		if(running){
			timer.cancel();
			running = false;
		} else {
			log.warn("Configuration reloading is already stopped!");
		}
	}

	@Override
	public void run(){
		this.reload();
	}
}