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
package lineage2.loginserver;

import lineage2.commons.util.Rnd;

public class SessionKey
{
	public final int playOkID1;
	public final int playOkID2;
	public final int loginOkID1;
	public final int loginOkID2;
	private final int hashCode;
	
	public SessionKey(int loginOK1, int loginOK2, int playOK1, int playOK2)
	{
		playOkID1 = playOK1;
		playOkID2 = playOK2;
		loginOkID1 = loginOK1;
		loginOkID2 = loginOK2;
		int hashCode = playOK1;
		hashCode *= 17;
		hashCode += playOK2;
		hashCode *= 37;
		hashCode += loginOK1;
		hashCode *= 51;
		hashCode += loginOK2;
		this.hashCode = hashCode;
	}
	
	public boolean checkLoginPair(int loginOk1, int loginOk2)
	{
		return (loginOkID1 == loginOk1) && (loginOkID2 == loginOk2);
	}
	
	public static SessionKey create()
	{
		return new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null)
		{
			return false;
		}
		if (o.getClass() == this.getClass())
		{
			SessionKey skey = (SessionKey) o;
			return (playOkID1 == skey.playOkID1) && (playOkID2 == skey.playOkID2) && skey.checkLoginPair(loginOkID1, loginOkID2);
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return hashCode;
	}
	
	@Override
	public String toString()
	{
		return new StringBuilder().append("[playOkID1: ").append(playOkID1).append(" playOkID2: ").append(playOkID2).append(" loginOkID1: ").append(loginOkID1).append(" loginOkID2: ").append(loginOkID2).append("]").toString();
	}
}
