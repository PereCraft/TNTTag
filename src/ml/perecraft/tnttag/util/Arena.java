/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.perecraft.tnttag.util;

import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author deka
 */
public class Arena {
    
    public static ArrayList<Arena> arenaObjects = new ArrayList<>();
    
    private Location lobbyLocation;
    private Location startLocation;
    private Location spectLocation;
    private String name;
    private String boardFooter;
    private ArrayList<UUID> players = new ArrayList<>();
    private ArrayList<UUID> tntPlayers = new ArrayList<>();
    private ArrayList<UUID> alivePlayers = new ArrayList<>();
    private int maxPlayers;
    private int minPlayers;
    private int taskId;
    private int seconds;
    private boolean inGame;
    private boolean runningCountdown;
    
    ScoreboardManager boardManager;
    Scoreboard board;
    Objective objective;
    
    public Arena(String name, Location lobbyLocation, Location startLocation, Location spectLocation, int maxPlayers, int minPlayers) {
        this.boardManager = Bukkit.getScoreboardManager();
        this.board = this.boardManager.getNewScoreboard();
        this.objective = this.board.registerNewObjective("lives", "dummy");
        this.name = name;
        this.lobbyLocation = lobbyLocation;
        this.startLocation = startLocation;
        this.spectLocation = spectLocation;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }
    
    public Location getLobbyLocation() {
        return this.lobbyLocation;
    }
    
    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }
    
    public Location getStartLocation() {
        return this.startLocation;
    }
    
    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }
    
    public Location getSpectatorLocation() {
        return this.spectLocation;
    }
    
    public void setSpectatorLocation(Location spectatorLocation) {
        this.spectLocation = spectatorLocation;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public int getMinPlayers() {
        return this.minPlayers;
    }
    
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }
    
    public ArrayList<UUID> getPlayers() {
        return this.players;
    }
    
    public ArrayList<UUID> getTntPlayers() {
        return this.tntPlayers;
    }
    
    public ArrayList<UUID> getAlivePlayers() {
        return this.alivePlayers;
    }
    
    public boolean isFull() {
        return this.players.size() >= this.maxPlayers;
    }
    
    public boolean isInGame() {
        return this.inGame;
    }
    
    public void setInGame(Boolean inGame) {
        this.inGame = inGame;
    }
    
    public boolean isRunningCountdown() {
        return this.runningCountdown;
    }
    
    public void setRunningCountdown(Boolean runningCountdown) {
        this.runningCountdown = runningCountdown;
    }
    
    public void sendMessage(String message) {
        for(UUID player : this.players) {
            Bukkit.getPlayer(player).sendMessage(message);
        }
    }
    
    public int getTaskId() {
        return taskId;
    }
    
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    
    public int getSeconds() {
        return this.seconds;
    }
    
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    
    public void setBoardFooter(String boardFooter) {
        this.boardFooter = boardFooter;
    }
    
    public void setBoard(Player player, int time) {
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName("§6§lTNTTAG");
        
        Score playerlist = this.objective.getScore("Player:");
        Score seconds = this.objective.getScore("Tempo:");
        
        playerlist.setScore(this.alivePlayers.size());
        seconds.setScore(time);
        
        player.setScoreboard(this.board);
    }
    
    public void removeBoard(Player player) {
        player.setScoreboard(boardManager.getNewScoreboard());
    }
}
