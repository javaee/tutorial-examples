/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.roster.request;

import java.util.List;
import javaeetutorial.roster.util.LeagueDetails;
import javaeetutorial.roster.util.PlayerDetails;
import javaeetutorial.roster.util.TeamDetails;
import javax.ejb.Remote;


@Remote
public interface Request {
    void addPlayer(String playerId, String teamId);

    void createLeague(LeagueDetails leagueDetails);

    void createPlayer(String id, String name, String position, double salary);

    void createTeamInLeague(TeamDetails teamDetails, String leagueId);

    void dropPlayer(String playerId, String teamId);

    List<PlayerDetails> getAllPlayers();

    LeagueDetails getLeague(String leagueId);

    List<LeagueDetails> getLeaguesOfPlayer(String playerId);

    PlayerDetails getPlayer(String playerId);

    List<PlayerDetails> getPlayersByCity(String city);

    List<PlayerDetails> getPlayersByHigherSalary(String name);

    List<PlayerDetails> getPlayersByLeagueId(String leagueId);

    List<PlayerDetails> getPlayersByPosition(String position);

    List<PlayerDetails> getPlayersByPositionAndName(String position, String name);

    List<PlayerDetails> getPlayersBySalaryRange(double low, double high);

    List<PlayerDetails> getPlayersBySport(String sport);

    List<PlayerDetails> getPlayersNotOnTeam();

    List<PlayerDetails> getPlayersOfTeam(String teamId);

    List<String> getSportsOfPlayer(String playerId);

    TeamDetails getTeam(String teamId);

    List<TeamDetails> getTeamsOfLeague(String leagueId);

    void removeLeague(String leagueId);

    void removePlayer(String playerId);

    void removeTeam(String teamId);
    
}
