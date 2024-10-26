
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
	//card class
	private class Card{
		//card value and suit
		String value;
		String suit;
		//card constructor
		Card(String value, String suit){
			this.value = value;
			this.suit = suit;
		}
		
		//toString method
		public String toString() {
			return value + " of " + suit;
		}
		
		//getValue method
		public int getValue() {
			//Ace Jack Queen King value (11-Ace / 10-JQK)
			if("AceJackQueenKing".contains(value)) {
				if(value == "Ace") {
					return 11;
				}
				return 10;
			}
			//2-10 value
			return Integer.parseInt(value);
		}
		
		//isAce method
		public boolean isAce() {
			return value == "Ace";
		}
		
		public String getImagePath() {
			return "./cards/" + toString() + ".png";
		}
	}
	//array list for cards
	ArrayList<Card> deck;
	//random for shuffle
	Random random = new Random();
	
	//dealer
	Card hiddenCard;
	ArrayList<Card> dealerHand;
	int dealerSum;
	int dealerAceCount;
	
	//player
	ArrayList<Card> playerHand;
	int playerSum;
	int playerAceCount;
	
	//window
	int boardWidth = 700;
	int boardHeight = 600;
	
	int cardWidth = 120;
	int cardHeight = 180;
	
	//JFrame JPanel
	JFrame frame = new JFrame("BLACKJACK");
	JPanel gamePanel = new JPanel() {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			try {
				//draw hidden card
				Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
				//after stand button hit - hidden card revealed
				if(!standButton.isEnabled()) {
					hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
				}
				g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);
				
				//draw dealers hand
				for(int i = 0; i < dealerHand.size(); i++) {
					Card card = dealerHand.get(i);
					Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
					g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
				}
				
				//draw players hand
				for(int i = 0; i < playerHand.size(); i++) {
					Card card = playerHand.get(i);
					Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
					g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
				}
				
				if(!standButton.isEnabled()) {
					dealerSum = reduceDealerAce();
					playerSum = reducePlayerAce();
					System.out.println("Stay: ");
					System.out.println(dealerSum);
					System.out.println(playerSum);
					
					String message = "";
					//if player bust - player lose
					if(playerSum > 21) {
						message = "You Lose!";
					}
					//if dealer bust - dealer lose
					else if(dealerSum > 21) {
						message = "You Win!";
					}
					//both have less than 21
					//if tie - tie
					else if(playerSum == dealerSum) {
						message = "Tie!";
					}
					//if player > dealer - player win
					else if(playerSum > dealerSum) {
						message = "You Win!";
					}
					//if player < dealer - dealer win
					else if(playerSum < dealerSum) {
						message = "You Lose!";
					}
					
					g.setFont(new Font("Arial", Font.PLAIN, 30));
					g.setColor(Color.white);
					g.drawString(message, 220, 250);
				}
				
			} catch(Exception e) {
				e.printStackTrace();			
				}
		}
	};
	JPanel buttonPanel = new JPanel();
	JButton hitButton = new JButton("Hit");
	JButton standButton = new JButton("Stand");
	
	//blackjack constructor
	BlackJack(){
		//starting game
		startGame();
		
		//frame visible, size, center of screen, restrict resizing, and manual close
		frame.setVisible(true);
		frame.setSize(boardWidth, boardHeight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//game panel border layout and set color to poker green
		gamePanel.setLayout(new BorderLayout());
		gamePanel.setBackground(new Color(53, 101, 77));
		frame.add(gamePanel); //add gamePanel
		
		//hit and stand button
		hitButton.setFocusable(false);
		buttonPanel.add(hitButton);
		standButton.setFocusable(false);
		buttonPanel.add(standButton);
		frame.add(buttonPanel, BorderLayout.SOUTH); //add buttonPanel
		
		hitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//additional cards from hitting 
				Card card = deck.remove(deck.size()-1);
				playerSum += card.getValue();
				playerAceCount += card.isAce()? 1 : 0;
				playerHand.add(card);
				//if players hand is over 21 then cant hit anymore
				if(reducePlayerAce() > 21) {
					hitButton.setEnabled(false);
				}
				
				gamePanel.repaint();
			}
		});
		
		standButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//after stand cant press hit or stand buttons anymore
				hitButton.setEnabled(false);
				standButton.setEnabled(false);
				
				while(dealerSum < 17) {
					//dealer keeps drawing until 17 or bust
					Card card = deck.remove(deck.size()-1);
					dealerSum += card.getValue();
					dealerAceCount += card.isAce() ? 1 : 0;
					dealerHand.add(card);
				}
				gamePanel.repaint();
			}
		});
		
		gamePanel.repaint();
	}
	
	//start game function
	public void startGame() {
		//deck
		getDeck();
		shuffleDeck();
		
		//dealer
		dealerHand = new ArrayList<Card>();
		dealerSum = 0;
		dealerAceCount = 0;
		
		//remove card out of deck for the hidden card from the end of the deck
		hiddenCard = deck.remove(deck.size()-1);
		dealerSum += hiddenCard.getValue();
		dealerAceCount += hiddenCard.isAce() ? 1 : 0;
		
		Card card = deck.remove(deck.size()-1);
		dealerSum += card.getValue();
		dealerAceCount += card.isAce() ? 1 : 0;
		dealerHand.add(card);
		
		System.out.println("Dealer: ");
		System.out.println(hiddenCard);
		System.out.println(dealerHand);
		System.out.println(dealerSum);
		System.out.println(dealerAceCount);
		
		//player
		playerHand = new ArrayList<Card>();
		playerSum = 0;
		playerAceCount = 0;
		
		for(int i = 0; i < 2; i++) {
			//remove card out of deck for the hidden card from the end of the deck
			card =  deck.remove(deck.size()-1);
			playerSum += card.getValue();
			playerAceCount += card. isAce() ? 1 : 0;
			playerHand.add(card);
		}
		
		System.out.println("Player: ");
		System.out.println(playerHand);
		System.out.println(playerSum);
		System.out.println(playerAceCount);
		
	}
	
	public void getDeck() {
		deck = new ArrayList<Card>();
		//card deck values and types
		String[] values = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
		String[] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};
		
		for(int i = 0; i < suits.length; i++) {
			for(int j = 0; j < values.length; j++) {
				//card object
				Card card = new Card(values[j], suits[i]);
				deck.add(card);
			}
		}
		
		System.out.println("GET DECK: ");
		System.out.println(deck);
	}
	
	//shuffle deck
	public void shuffleDeck() {
		for(int i = 0; i < deck.size(); i++) {
			int j = random.nextInt(deck.size());
			Card currentCard = deck.get(i);
			Card randomCard = deck.get(j);
			deck.set(i, randomCard);
			deck.set(j, currentCard);
		}
		
		System.out.println("Shuffled Deck!");
		System.out.println(deck);
	}
	
	public int reducePlayerAce() {
		//make A worth 1 point instead of 10 when needed aka when greater than 21
		while(playerSum > 21 && playerAceCount > 0) {
			playerSum -= 10;
			playerAceCount -= 1;
		}
		return playerSum;
	}
	
	public int reduceDealerAce() {
		//same thing but for dealer
		while(dealerSum > 21 && dealerAceCount > 0) {
			dealerSum -= 10;
			dealerAceCount -= 1;
		}
		return dealerSum;
	}
}
