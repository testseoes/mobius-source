/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lineage2.commons.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggerObject
{
	protected final Logger _log = LoggerFactory.getLogger(getClass());
	
	public void error(String st, Exception e)
	{
		_log.error(getClass().getSimpleName() + ": " + st, e);
	}
	
	public void error(String st)
	{
		_log.error(getClass().getSimpleName() + ": " + st);
	}
	
	public void warn(String st, Exception e)
	{
		_log.warn(getClass().getSimpleName() + ": " + st, e);
	}
	
	public void warn(String st)
	{
		_log.warn(getClass().getSimpleName() + ": " + st);
	}
	
	public void info(String st, Exception e)
	{
		_log.info(getClass().getSimpleName() + ": " + st, e);
	}
	
	public void info(String st)
	{
		_log.info(getClass().getSimpleName() + ": " + st);
	}
}
