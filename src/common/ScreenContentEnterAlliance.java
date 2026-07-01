package common;

import java.io.Serializable;

@SuppressWarnings("serial")
class ScreenContentEnterAlliance implements Serializable
{
	private String title;
	private byte titleColorIndex;
	private ScreenContentPlanetEditorPlayerInfo[] playerInfos;
	private int[] allianceMembersCurrent;
	private boolean[] allianceMembersNew;
	
	ScreenContentEnterAlliance(
			String title,
			byte titleColorIndex,
			ScreenContentPlanetEditorPlayerInfo[] playerInfos,
			int[] allianceMembersCurrent,
			boolean[] allianceMembersNew)
	{
		super();
		this.title = title;
		this.titleColorIndex = titleColorIndex;
		this.playerInfos = playerInfos;
		this.allianceMembersCurrent = allianceMembersCurrent;
		this.allianceMembersNew = allianceMembersNew;
	}

	String getTitle()
	{
		return title;
	}

	byte getTitleColorIndex()
	{
		return titleColorIndex;
	}

	ScreenContentPlanetEditorPlayerInfo[] getPlayerInfos()
	{
		return playerInfos;
	}

	int[] getAllianceMembersCurrent()
	{
		return allianceMembersCurrent;
	}

	boolean[] getAllianceMembersNew()
	{
		return allianceMembersNew;
	}
}
