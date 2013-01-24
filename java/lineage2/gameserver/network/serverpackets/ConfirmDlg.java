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
package lineage2.gameserver.network.serverpackets;

import lineage2.gameserver.network.serverpackets.components.SystemMsg;

public class ConfirmDlg extends SysMsgContainer<ConfirmDlg>
{
	private final int _time;
	private int _requestId;
	
	public ConfirmDlg(SystemMsg msg, int time)
	{
		super(msg);
		_time = time;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xF3);
		writeElements();
		writeD(_time);
		writeD(_requestId);
	}
	
	public void setRequestId(int requestId)
	{
		_requestId = requestId;
	}
}
