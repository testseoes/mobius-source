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
package quests;

import lineage2.gameserver.model.instances.NpcInstance;
import lineage2.gameserver.model.quest.Quest;
import lineage2.gameserver.model.quest.QuestState;
import lineage2.gameserver.scripts.ScriptFile;

public class _608_SlayTheEnemyCommander extends Quest implements ScriptFile
{
	private static final int KADUN_ZU_KETRA = 31370;
	private static final int VARKAS_COMMANDER_MOS = 25312;
	private static final int HEAD_OF_MOS = 7236;
	private static final int TOTEM_OF_WISDOM = 7220;
	private static final int MARK_OF_KETRA_ALLIANCE4 = 7214;
	private static final int MARK_OF_KETRA_ALLIANCE5 = 7215;
	
	@Override
	public void onLoad()
	{
	}
	
	@Override
	public void onReload()
	{
	}
	
	@Override
	public void onShutdown()
	{
	}
	
	public _608_SlayTheEnemyCommander()
	{
		super(true);
		addStartNpc(KADUN_ZU_KETRA);
		addKillId(VARKAS_COMMANDER_MOS);
		addQuestItem(HEAD_OF_MOS);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if (event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "elder_kadun_zu_ketra_q0608_0104.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("608_3"))
		{
			if (st.getQuestItemsCount(HEAD_OF_MOS) >= 1)
			{
				htmltext = "elder_kadun_zu_ketra_q0608_0201.htm";
				st.takeItems(HEAD_OF_MOS, -1);
				st.giveItems(TOTEM_OF_WISDOM, 1);
				st.addExpAndSp(0, 10000);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "elder_kadun_zu_ketra_q0608_0106.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		
		if (cond == 0)
		{
			if (st.getPlayer().getLevel() >= 75)
			{
				if ((st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE4) == 1) || (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE5) == 1))
				{
					htmltext = "elder_kadun_zu_ketra_q0608_0101.htm";
				}
				else
				{
					htmltext = "elder_kadun_zu_ketra_q0608_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "elder_kadun_zu_ketra_q0608_0103.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if ((cond == 1) && (st.getQuestItemsCount(HEAD_OF_MOS) == 0))
		{
			htmltext = "elder_kadun_zu_ketra_q0608_0106.htm";
		}
		else if ((cond == 2) && (st.getQuestItemsCount(HEAD_OF_MOS) >= 1))
		{
			htmltext = "elder_kadun_zu_ketra_q0608_0105.htm";
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1)
		{
			st.giveItems(HEAD_OF_MOS, 1);
			st.setCond(2);
			st.playSound(SOUND_ITEMGET);
		}
		
		return null;
	}
}