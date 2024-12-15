package com.aor.ZombieZone.Model;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class Enemy extends Element implements HasLife,HasMovement {
    protected int life;
    protected int speed;
    protected long elapsed_time;

    public Enemy(int x,int y){
        super(x,y);
    }

    @Override
    public Position getPosition() {
        return super.getPosition();
    }

    public abstract void draw(TextGraphics screen);

    public void updateZombieWalk(Soldier soldier,Game game,long deltaTime){
        elapsed_time += deltaTime;
        int timeToMove = 1000/speed;

        while(elapsed_time>=timeToMove){
            track(soldier, game);
            elapsed_time -= timeToMove;
        }
    }

    public void track(Soldier soldier, Game game) {
        int[][] places = new int[game.getArena().getHeight()+10][game.getArena().getWidth()+10];
        List<Position> positionsOfWalls = game.getPositionsWalls();

        Position soldierPosition = soldier.getPosition();

        for(Position position : positionsOfWalls){
            places[position.getX()][position.getY()] = 2;
        }
        List<Position> positionsOfZombies = game.getPositionsZombies();
        for(Position position : positionsOfZombies){
            if(position != soldierPosition) {
                places[position.getX()][position.getY()] = 2;
            }
        }

        TraceToHero(this.getPosition(), soldierPosition , places , game);
    }
    public void TraceToHero(Position zombiePosition, Position soldierPosition, int[][] places, Game game) {
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        //Resolução feita por BFS, kkk demos isso em AED na semana que estou fazendo isso, e realmente ajudou !
        Queue<Position> queue = new LinkedList<>();
        boolean[][] visited = new boolean[places.length][places[0].length];
        Position[][] predecessor = new Position[places.length][places[0].length];

        queue.add(zombiePosition);
        visited[zombiePosition.getX()][zombiePosition.getY()] = true;
        predecessor[zombiePosition.getX()][zombiePosition.getY()] = null;
        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (current.equals(soldierPosition)) {
                break;
            }
            for (int i = 0; i < 4; i++) {
                int newX = current.getX() + dx[i];
                int newY = current.getY() + dy[i];
                Position nextPosition = new Position(newX, newY);

                if (newX >= 0 && newY >= 0 && newX < places.length && newY < places[0].length && places[newX][newY] != 2 && !visited[newX][newY]) {

                    visited[newX][newY] = true;
                    predecessor[newX][newY] = current;
                    queue.add(nextPosition);
                }
            }
        }
        //Descontrução do step até o player
        Position step = soldierPosition;
        while (predecessor[step.getX()][step.getY()] != null && !predecessor[step.getX()][step.getY()].equals(zombiePosition)) {
            step = predecessor[step.getX()][step.getY()];
        }
        if (step != null) {
            this.setPosition(step);
        }
    }
    @Override
    public void moveUp() {
        Position newPosition = new Position(getPosition().x, getPosition().y - 1);
        this.setPosition(newPosition);
    }

    @Override
    public void moveDown() {
        Position newPosition = new Position(getPosition().x, getPosition().y + 1);
        this.setPosition(newPosition);
    }

    @Override
    public void moveLeft() {
        Position newPosition = new Position(getPosition().x - 1, getPosition().y);
        this.setPosition(newPosition);
    }

    @Override
    public void moveRight() {
        Position newPosition = new Position(getPosition().x + 1, getPosition().y);
        this.setPosition(newPosition);
    }

    @Override
    public void hit() {
        life --;
    }

    @Override
    public boolean isDead() {return life <= 0;
    }
}
