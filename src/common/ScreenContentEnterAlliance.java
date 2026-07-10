package common;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
class ScreenContentEnterAlliance implements Serializable
{
	private String title;
	private byte titleColorIndex;
	private ScreenContentPlanetEditorPlayerInfo[] playerInfos;
	private int[] allianceMembersCurrent;
	private boolean[] allianceMembersNew;
	private ArrayList<String> explanations;
	
	ScreenContentEnterAlliance(
			String title,
			byte titleColorIndex,
			ScreenContentPlanetEditorPlayerInfo[] playerInfos,
			int[] allianceMembersCurrent,
			boolean[] allianceMembersNew,
			ArrayList<String> explanations)
	{
		super();
		this.title = title;
		this.titleColorIndex = titleColorIndex;
		this.playerInfos = playerInfos;
		this.allianceMembersCurrent = allianceMembersCurrent;
		this.allianceMembersNew = allianceMembersNew;
		this.explanations = explanations;
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
	
	ArrayList<String> getExplanations()
	{
		return explanations;
	}
}
