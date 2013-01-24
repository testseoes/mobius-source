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
package lineage2.loginserver.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage2.commons.dbutils.DbUtils;
import lineage2.commons.net.utils.Net;
import lineage2.commons.net.utils.NetList;
import lineage2.loginserver.database.L2DatabaseFactory;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Account
{
	private final static Logger _log = LoggerFactory.getLogger(Account.class);
	private final String login;
	private String passwordHash;
	private String allowedIP;
	private final NetList allowedIpList = new NetList();
	private int accessLevel;
	private int banExpire;
	private double bonus;
	private int bonusExpire;
	private String lastIP;
	private int lastAccess;
	private int lastServer;
	private final IntObjectMap<Pair<Integer, int[]>> _serversInfo = new HashIntObjectMap<>(2);
	
	public Account(String login)
	{
		this.login = login;
	}
	
	public String getLogin()
	{
		return login;
	}
	
	public String getPasswordHash()
	{
		return passwordHash;
	}
	
	public void setPasswordHash(String passwordHash)
	{
		this.passwordHash = passwordHash;
	}
	
	public String getAllowedIP()
	{
		return allowedIP;
	}
	
	public boolean isAllowedIP(String ip)
	{
		return allowedIpList.isEmpty() || allowedIpList.isInRange(ip);
	}
	
	public void setAllowedIP(String allowedIP)
	{
		allowedIpList.clear();
		this.allowedIP = allowedIP;
		if (allowedIP.isEmpty())
		{
			return;
		}
		String[] masks = allowedIP.split("[\\s,;]+");
		for (String mask : masks)
		{
			allowedIpList.add(Net.valueOf(mask));
		}
	}
	
	public int getAccessLevel()
	{
		return accessLevel;
	}
	
	public void setAccessLevel(int accessLevel)
	{
		this.accessLevel = accessLevel;
	}
	
	public double getBonus()
	{
		return bonus;
	}
	
	public void setBonus(double bonus)
	{
		this.bonus = bonus;
	}
	
	public int getBonusExpire()
	{
		return bonusExpire;
	}
	
	public void setBonusExpire(int bonusExpire)
	{
		this.bonusExpire = bonusExpire;
	}
	
	public int getBanExpire()
	{
		return banExpire;
	}
	
	public void setBanExpire(int banExpire)
	{
		this.banExpire = banExpire;
	}
	
	public void setLastIP(String lastIP)
	{
		this.lastIP = lastIP;
	}
	
	public String getLastIP()
	{
		return lastIP;
	}
	
	public int getLastAccess()
	{
		return lastAccess;
	}
	
	public void setLastAccess(int lastAccess)
	{
		this.lastAccess = lastAccess;
	}
	
	public int getLastServer()
	{
		return lastServer;
	}
	
	public void setLastServer(int lastServer)
	{
		this.lastServer = lastServer;
	}
	
	public void addAccountInfo(int serverId, int size, int[] deleteChars)
	{
		_serversInfo.put(serverId, new ImmutablePair<>(size, deleteChars));
	}
	
	public Pair<Integer, int[]> getAccountInfo(int serverId)
	{
		return _serversInfo.get(serverId);
	}
	
	@Override
	public String toString()
	{
		return login;
	}
	
	public void restore()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT password, access_level, ban_expire, allow_ip, bonus, bonus_expire, last_server, last_ip, last_access FROM accounts WHERE login = ?");
			statement.setString(1, login);
			rset = statement.executeQuery();
			if (rset.next())
			{
				setPasswordHash(rset.getString("password"));
				setAccessLevel(rset.getInt("access_level"));
				setBanExpire(rset.getInt("ban_expire"));
				setAllowedIP(rset.getString("allow_ip"));
				setBonus(rset.getDouble("bonus"));
				setBonusExpire(rset.getInt("bonus_expire"));
				setLastServer(rset.getInt("last_server"));
				setLastIP(rset.getString("last_ip"));
				setLastAccess(rset.getInt("last_access"));
			}
		}
		catch (Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}
	
	public void save()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO accounts (login, password) VALUES(?,?)");
			statement.setString(1, getLogin());
			statement.setString(2, getPasswordHash());
			statement.execute();
		}
		catch (Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
	
	public void update()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET password = ?, access_level = ?, ban_expire = ?, allow_ip = ?, bonus = ?, bonus_expire = ?, last_server = ?, last_ip = ?, last_access = ? WHERE login = ?");
			statement.setString(1, getPasswordHash());
			statement.setInt(2, getAccessLevel());
			statement.setInt(3, getBanExpire());
			statement.setString(4, getAllowedIP());
			statement.setDouble(5, getBonus());
			statement.setInt(6, getBonusExpire());
			statement.setInt(7, getLastServer());
			statement.setString(8, getLastIP());
			statement.setInt(9, getLastAccess());
			statement.setString(10, getLogin());
			statement.execute();
		}
		catch (Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
