package com.itlgl.swing.gobang;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class CBoardPanel extends JPanel implements MouseListener {
    private static final int START_X = 20;
    private static final int START_Y = 20;
    private static final int STEP = 35;
    private static final int ROW = 15;
    private static final int COL = 15;

    public static final int BLANK = 1;
    public static final int WHITE = -1;
    public static final int NONE = 0;

    public static final int AI_FIRST = 1;
    public static final int HUMAN_FIRST = 2;

    public static final int EASY = 2;
    public static final int HARD = 4;

    private BufferedImage chessBoardImage = null, whiteImage = null, blankImage = null;//图片
    private int[][] chesses = new int[ROW][COL];//棋盘情况
    private int aiColor, humColor;//电脑和人的棋子颜色
    private int level;//当前难度
    private ChessBoard cb;//主类的引用
    private boolean isHumTurn = false;//是不是轮到人下的布尔值，true代表轮到人下
    private int currRow, currCol;//当前棋子的行和列
    private int winner = NONE;//表示赢家的颜色，初始为空
    private ArrayList<Point> chessProcess = new ArrayList<Point>();//表示每一个棋子下的先后顺序，用于悔棋

    private static final int FIVE = 10000;//能成五连
    private static final int L_FOUR = 2000;//能成活四
    private static final int D_FOUR = 1200;//能成死四
    private static final int L_THREE = 800;//能成活三
    private static final int D_THREE = 180;//能成死三
    private static final int L_TWO = 80;//能成活二
    private static final int D_TWO = 20;//能成死二

    private static final int HENG = 1;
    private static final int SHU = 2;
    private static final int XIE45 = 3;
    private static final int XIE135 = 4;

    public CBoardPanel(int whoFirst, int level, ChessBoard cb)//构造器
    {
        this.cb = cb;
        this.level = level;
        chesses = new int[ROW][COL];
        winner = NONE;
        if (whoFirst == HUMAN_FIRST) {
            isHumTurn = true;
            humColor = BLANK;
            aiColor = WHITE;
        } else {
            humColor = WHITE;
            aiColor = BLANK;
            isHumTurn = false;
            //电脑走一步棋
            aiGoOneStep();
        }
        try {
            // http://www.cnblogs.com/javayuer/archive/2011/01/02/1924192.html
            // Class.getResourceAsStream(String path) ： path 不以’/'开头时默认是从此类所在的包下取资源，以’/'开头则是从
            // ClassPath根下获取。其只是通过path构造一个绝对路径，最终还是由ClassLoader获取资源。
            chessBoardImage = ImageIO.read(CBoardPanel.class.getResourceAsStream("/chessboard.jpg"));
            blankImage = ImageIO.read(CBoardPanel.class.getResourceAsStream("/b.jpg"));
            whiteImage = ImageIO.read(CBoardPanel.class.getResourceAsStream("/w.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.addMouseListener(this);
    }

    public void restart(int whoFirst, int level, ChessBoard cb)//重新开始的方法
    {
        this.level = level;
        chesses = new int[ROW][COL];
        winner = NONE;
        chessProcess.clear();
        if (whoFirst == HUMAN_FIRST) {
            isHumTurn = true;
            humColor = BLANK;
            aiColor = WHITE;
        } else {
            humColor = WHITE;
            aiColor = BLANK;
            isHumTurn = false;
            //电脑走一步棋
            aiGoOneStep();
        }
        this.repaint();
    }

    public void paintComponent(Graphics g)//重写的paintComponent方法
    {
        super.paintComponents(g);

        g.drawImage(chessBoardImage, 0, 0, this);
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (chesses[i][j] == BLANK) {
                    drawImageInRowCol(i, j, blankImage, g);
                } else if (chesses[i][j] == WHITE) {
                    drawImageInRowCol(i, j, whiteImage, g);
                }
            }
        }
        g.setColor(Color.RED);
        if (chessProcess.size() > 0) {
            g.drawRect(currCol * STEP + 2, currRow * STEP + 2, STEP, STEP);
        }
    }

    public void back()//退一步棋
    {
        if (!isHumTurn || chessProcess.size() < 2) {
            return;
        }
        winner = NONE;
        Point p = chessProcess.get(chessProcess.size() - 1);
        chesses[p.x][p.y] = NONE;
        chessProcess.remove(chessProcess.size() - 1);

        p = chessProcess.get(chessProcess.size() - 1);
        chesses[p.x][p.y] = NONE;
        chessProcess.remove(chessProcess.size() - 1);
        if (chessProcess.size() > 0) {
            p = chessProcess.get(chessProcess.size() - 1);
            currRow = p.x;
            currCol = p.y;
        }

        this.repaint();
    }

    public void setLevel(int level)//设置难易程度
    {
        this.level = level;
    }

    private void drawImageInRowCol(int row, int col, BufferedImage image, Graphics g)//在指定行和列画棋子
    {
        int x = col * STEP + 3;
        int y = row * STEP + 3;
        g.drawImage(image, x, y, this);
    }

    private Point pressWhere(MouseEvent e)//判断鼠标的点击位置，转换为行和列
    {
        int x = e.getPoint().x;
        int y = e.getPoint().y;
        int row = (y - START_Y + STEP / 2) / STEP;
        int col = (x - START_X + STEP / 2) / STEP;
        return new Point(row, col);
    }

    private void goOneStep(int row, int col, int color)//生成一步棋，并判断是否有玩家胜出
    {
        chesses[row][col] = color;
        chessProcess.add(new Point(row, col));
        currRow = row;
        currCol = col;
        this.repaint();
        boolean flag = isWin(currRow, currCol, color);
        if (flag) {
            winner = color;
            String msg;
            if (winner == BLANK) {
                msg = "黑方胜啦！";
            } else {
                msg = "白方胜啦！";
            }
            JOptionPane.showMessageDialog(cb, msg, "游戏结束", JOptionPane.INFORMATION_MESSAGE);
        }
        if (color == humColor && winner == NONE) {
            aiGoOneStep();
        }
    }

    private int score(int row, int col, int dir, int color)//判断当前点指定方向的棋形
    {
        if (row < 0 || row >= ROW || col < 0 || col >= COL || chesses[row][col] != NONE) {
            return 0;
        }
        int x = 0, y = 0;
        boolean lflag = false, rflag = false;//左右标志位
        int qx = 1;
        int ltemp = 0, rtemp = 0;
        switch (dir) {
            case HENG:
                x = 1;
                y = 0;
                break;
            case SHU:
                x = 0;
                y = 1;
                break;
            case XIE45:
                x = 1;
                y = 1;
                break;
            case XIE135:
                x = 1;
                y = -1;
                break;
        }
        int currRow = row, currCol = col;
        for (int i = 1; i <= 5; i++)//往左看
        {
            currRow = row - i * y;
            currCol = col - i * x;
            if (currRow < 0 || currRow >= ROW || currCol < 0 || currCol >= COL) {
                lflag = true;
                break;
            }
            if (chesses[currRow][currCol] == color) {
                qx += 1;
            } else if (chesses[currRow][currCol] == -color) {
                lflag = true;
                break;
            } else if (chesses[currRow][currCol] == NONE) {
                for (int j = 1; j < 5; j++) {
                    currRow = row - i * y;
                    currCol = col - i * x;
                    if (chesses[currRow][currCol] == color) {
                        ltemp++;
                    } else if (chesses[currRow][currCol] == -color) {
                        lflag = true;
                        break;
                    } else {
                        break;
                    }
                }
                break;
            }
        }
        for (int i = 1; i <= 5; i++)//往右看
        {
            currRow = row + i * y;
            currCol = col + i * x;
            if (currRow < 0 || currRow >= ROW || currCol < 0 || currCol >= COL) {
                rflag = true;
                break;
            }
            if (chesses[currRow][currCol] == color) {
                qx += 1;
            } else if (chesses[currRow][currCol] == -color) {
                rflag = true;
                break;
            } else if (chesses[currRow][currCol] == NONE) {
                for (int j = 1; j < 5; j++) {
                    currRow = row + i * y;
                    currCol = col + i * x;
                    if (chesses[currRow][currCol] == color) {
                        rtemp++;
                    } else if (chesses[currRow][currCol] == -color) {
                        rflag = true;
                        break;
                    } else {
                        break;
                    }
                }
                break;
            }
        }
        int temp = Math.max(ltemp, rtemp);
//System.out.println("qx="+qx);
        if (qx >= 5) {
            return FIVE;
        } else if (lflag && rflag) {
            return 0;
        } else {
            qx += temp;
            if (lflag || rflag) {
                switch (qx) {
                    case 1:
                        return 0;
                    case 2:
                        return D_TWO;
                    case 3:
                        return D_THREE;
                    case 4:
                        return D_FOUR;
                }
            } else {
                if (temp > 0) {
                    switch (qx) {
                        case 1:
                            return 0;
                        case 2:
                            return D_TWO;
                        case 3:
                            return D_THREE;
                        case 4:
                            return D_FOUR;
                    }
                } else {
                    switch (qx) {
                        case 1:
                            return 0;
                        case 2:
                            return L_TWO;
                        case 3:
                            return L_THREE;
                        case 4:
                            return L_FOUR;
                    }
                }
            }
        }
        return 0;
    }

    private int getScore(int row, int col, int color) {
        int result = 0;
        int heng = score(row, col, HENG, color);
        int hengl = score(row, col, HENG, -color);
        int shu = score(row, col, SHU, color);
        int shul = score(row, col, SHU, -color);
        int xie45 = score(row, col, XIE45, color);
        int xie45l = score(row, col, XIE45, -color);
        int xie135 = score(row, col, XIE135, color);
        int xie135l = score(row, col, XIE135, -color);
		/*int[] temp=new int[8];
		temp[heng]++;
		temp[shu]++;
		temp[xie45]++;
		temp[xie135]++;
		if()*/
        result = heng + shu + xie45 + xie135 + hengl + shul + xie45l + xie135l;

        return result;
    }

    private boolean isWin(int row, int col, int color)//判断当前点是否有五连的情况
    {
        int max = 0;
        int temp = 0;
        //判断横向
        for (int i = 0; i < COL; i++) {
            if (chesses[row][i] == color) {
                temp++;
                if (max < temp) {
                    max = temp;
                }
            } else {
                temp = 0;
            }
        }
        if (max >= 5) {
            return true;
        }
        //判断纵向
        temp = 0;
        max = 0;
        for (int i = 0; i < ROW; i++) {
            if (chesses[i][col] == color) {
                temp++;
                if (max < temp) {
                    max = temp;
                }
            } else {
                temp = 0;
            }
        }
        if (max >= 5) {
            return true;
        }
        //判断右下方向
        int x = row, y = col;
        max = 0;
        while (x >= 0 && x < ROW && y >= 0 && y < COL && chesses[x][y] == color) {
            x--;
            y--;
        }
        x++;
        y++;
        while (x >= 0 && x < ROW && y >= 0 && y < COL && chesses[x][y] == color) {
            max++;
            x++;
            y++;
        }
        if (max >= 5) {
            return true;
        }
        //判断右上方向
        x = row;
        y = col;
        max = 0;
        while (x >= 0 && x < ROW && y >= 0 && y < COL && chesses[x][y] == color) {
            x--;
            y++;
        }
        x++;
        y--;
        while (x >= 0 && x < ROW && y >= 0 && y < COL && chesses[x][y] == color) {
            max++;
            x++;
            y--;
        }
        if (max >= 5) {
            return true;
        }
        return false;
    }

    private class AI extends Thread//电脑走棋的线程
    {
        @Override
        public void run() {
            isHumTurn = false;
            int row = (int) (Math.random() * ROW);
            int col = (int) (Math.random() * COL);
            while (chesses[row][col] != 0) {
                row = (int) (Math.random() * ROW);
                col = (int) (Math.random() * COL);
            }

            int score = 0;
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    if (chesses[i][j] != 0) {
                        //System.out.print(i+","+j+">"+chesses[i][j]+" ");
                        continue;
                    }
                    int temp = getScore(i, j, aiColor);
                    //System.out.print(i+","+j+"="+temp+" ");
                    if (score < temp) {
                        row = i;
                        col = j;
                        score = temp;
                    }
                }
                //System.out.println();
            }
            goOneStep(row, col, aiColor);
            isHumTurn = true;
        }
    }

    private void aiGoOneStep()//电脑走一步棋的方法
    {
		/*AI ai=new AI();
		ai.start();*/

        isHumTurn = false;
        int row = 0;
        int col = 0;
        if (chessProcess.size() == 0) {
            row = 7;
            col = 7;
            goOneStep(row, col, aiColor);
            isHumTurn = true;
            return;
        }

        int score = 0;
        //System.out.println(aiColor);
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (chesses[i][j] != 0) {
                    //System.out.print(i+","+j+">"+chesses[i][j]+" ");
                    continue;
                }
                int temp = getScore(i, j, aiColor);
                //System.out.print(i+","+j+"="+temp+" ");
                if (score < temp) {
                    row = i;
                    col = j;
                    score = temp;
                }
            }
            //System.out.println();
        }
        goOneStep(row, col, aiColor);
        isHumTurn = true;
    }

    //重写鼠标监听事件
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e)//当鼠标抬起时触发，玩家在指定位置走一步棋
    {
        if (!isHumTurn || winner != NONE) {
            return;
        }
        Point p = pressWhere(e);
        if (chesses[p.x][p.y] != 0) {
            return;
        }
        isHumTurn = false;
        goOneStep(p.x, p.y, humColor);
    }

    public void dianNaoJianQi() {
        if (!isHumTurn || winner != NONE) {
            return;
        }
        int row = (int) (Math.random() * ROW);
        int col = (int) (Math.random() * COL);
        while (chesses[row][col] != 0) {
            row = (int) (Math.random() * ROW);
            col = (int) (Math.random() * COL);
        }

        int score = 0;
        //System.out.println(aiColor);
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (chesses[i][j] != 0) {
                    //System.out.print(i+","+j+">"+chesses[i][j]+" ");
                    continue;
                }
                int temp = getScore(i, j, humColor);
                //System.out.print(i+","+j+"="+temp+" ");
                if (score < temp) {
                    row = i;
                    col = j;
                    score = temp;
                }
            }
            //System.out.println();
        }
        goOneStep(row, col, humColor);
    }
}
