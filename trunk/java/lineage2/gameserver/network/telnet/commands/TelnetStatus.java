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
package lineage2.gameserver.network.telnet.commands;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import lineage2.commons.lang.StatsUtils;
import lineage2.gameserver.Config;
import lineage2.gameserver.GameTimeController;
import lineage2.gameserver.Shutdown;
import lineage2.gameserver.instancemanager.ReflectionManager;
import lineage2.gameserver.model.World;
import lineage2.gameserver.network.telnet.TelnetCommand;
import lineage2.gameserver.network.telnet.TelnetCommandHolder;
import lineage2.gameserver.tables.GmListTable;
import lineage2.gameserver.utils.Util;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * @author Mobius
 * @version $Revision: 1.0 $
 */
public class TelnetStatus implements TelnetCommandHolder
{
	private final Set<TelnetCommand> _commands = new LinkedHashSet<>();
	
	/**
	 * Constructor for TelnetStatus.
	 */
	public TelnetStatus()
	{
		_commands.add(new TelnetCommand("status", "s")
		{
			@Override
			public String getUsage()
			{
				return "status";
			}
			
			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();
				int[] stats = World.getStats();
				sb.append("Server Status: ").append('\n');
				sb.append("Players: ................. ").append(stats[12]).append('/').append(Config.MAXIMUM_ONLINE_USERS).append('\n');
				sb.append("     Online: ............. ").append(stats[12] - stats[13]).append('\n');
				sb.append("     Offline: ............ ").append(stats[13]).append('\n');
				sb.append("     GM: ................. ").append(GmListTable.getAllGMs().size()).append('\n');
				sb.append("Objects: ................. ").append(stats[10]).append('\n');
				sb.append("Characters: .............. ").append(stats[11]).append('\n');
				sb.append("Summons: ................. ").append(stats[18]).append('\n');
				sb.append("Npcs: .................... ").append(stats[15]).append('/').append(stats[14]).append('\n');
				sb.append("Monsters: ................ ").append(stats[16]).append('\n');
				sb.append("Minions: ................. ").append(stats[17]).append('\n');
				sb.append("Doors: ................... ").append(stats[19]).append('\n');
				sb.append("Items: ................... ").append(stats[20]).append('\n');
				sb.append("Reflections: ............. ").append(ReflectionManager.getInstance().getAll().length).append('\n');
				sb.append("Regions: ................. ").append(stats[0]).append('\n');
				sb.append("     Active: ............. ").append(stats[1]).append('\n');
				sb.append("     Inactive: ........... ").append(stats[2]).append('\n');
				sb.append("     Null: ............... ").append(stats[3]).append('\n');
				sb.append("Game Time: ............... ").append(getGameTime()).append('\n');
				sb.append("Real Time: ............... ").append(getCurrentTime()).append('\n');
				sb.append("Start Time: .............. ").append(getStartTime()).append('\n');
				sb.append("Uptime: .................. ").append(getUptime()).append('\n');
				sb.append("Shutdown: ................ ").append(Util.formatTime(Shutdown.getInstance().getSeconds())).append('/').append(Shutdown.getInstance().getMode()).append('\n');
				sb.append("Threads: ................. ").append(Thread.activeCount()).append('\n');
				sb.append("RAM Used: ................ ").append(StatsUtils.getMemUsedMb()).append('\n');
				return sb.toString();
			}
		});
	}
	
	/**
	 * Method getCommands.
	 * @return Set<TelnetCommand> * @see lineage2.gameserver.network.telnet.TelnetCommandHolder#getCommands()
	 */
	@Override
	public Set<TelnetCommand> getCommands()
	{
		return _commands;
	}
	
	/**
	 * Method getGameTime.
	 * @return String
	 */
	static String getGameTime()
	{
		int t = GameTimeController.getInstance().getGameTime();
		int h = t / 60;
		int m = t % 60;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		return format.format(cal.getTime());
	}
	
	/**
	 * Method getUptime.
	 * @return String
	 */
	static String getUptime()
	{
		return DurationFormatUtils.formatDurationHMS(ManagementFactory.getRuntimeMXBean().getUptime());
	}
	
	/**
	 * Method getStartTime.
	 * @return String
	 */
	static String getStartTime()
	{
		return new Date(ManagementFactory.getRuntimeMXBean().getStartTime()).toString();
	}
	
	/**
	 * Method getCurrentTime.
	 * @return String
	 */
	static String getCurrentTime()
	{
		return new Date().toString();
	}
}