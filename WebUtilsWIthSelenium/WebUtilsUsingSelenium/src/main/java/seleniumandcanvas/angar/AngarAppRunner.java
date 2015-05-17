package seleniumandcanvas.angar;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class AngarAppRunner {
	final WebDriver angarDriver ;
	JavascriptExecutor js ;

	Random random = new Random();
	WebElement canvas;
	Actions builder;
	int[] canvasDefaultColor = {242, 251, 255};

	public AngarAppRunner(){
		this.angarDriver = new FirefoxDriver();
		this.builder = new Actions(this.angarDriver);
		this.js = (JavascriptExecutor) angarDriver;
	}

	public void go() {
		login();
		startGame();

	}

	public void login(){
		angarDriver.get("http://agar.io/");
		angarDriver.manage().window().maximize();
		angarDriver.findElement(By.id("nick")).sendKeys("Animator" + random.nextInt(1000));
		Select dropdown = new Select(angarDriver.findElement(By.id("region")));
		dropdown.selectByIndex(1);
		angarDriver.findElement(By.id("playBtn")).click();
		this.canvas = angarDriver.findElement(By.id("canvas"));
	}


	public void startGame() {
		builder.moveToElement(canvas, getOwnCoordinates()[0], getOwnCoordinates()[1]).click().build().perform();


		while(true){
			int[] nextMove = generateMove();
			//System.out.println("MOVING to " + nextMove[0] + " " + nextMove[1]);
			builder.moveToElement(canvas, nextMove[0], nextMove[1]).click().build().perform();
			/*try {
				Thread.sleep(52);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
	}

	public int[] getCanvasColor(int[] coord){
		int [] result = new int[3];
		String jsCode = "document.getElementById('canvas');context = canvas.getContext('2d');"
				+ "var data = context.getImageData(%s,%s,1,1).data; return data[0] + ' ' + data[1] + ' ' + data[2]";
		jsCode = String.format(jsCode, coord[0], coord[1]);

		// execute native js
		String jsResult = (String) js.executeScript(jsCode);
		result[0] = Integer.valueOf(jsResult.split(" ")[0]);
		result[1] = Integer.valueOf(jsResult.split(" ")[1]);
		result[2] = Integer.valueOf(jsResult.split(" ")[2]);

		return result;
	}

	public int [] getOwnCoordinates(){
		int [] result = new int[2];
		result[0] = canvas.getSize().width/2;
		result[1] = canvas.getSize().height/2-20;

		return result;
	}

	public int [] generateMove(){
		int[] currentPos = getOwnCoordinates();
		int[] resultNextMove = new int[2];
		String availableDirections = "";


		if(Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0] - 170, currentPos[1]})) &&
				Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0] - 70, currentPos[1]})) &&
				Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0] - 40, currentPos[1]}))
				){
			availableDirections +="L";
		}
		if(Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0], currentPos[1]-170})) &&
				Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0], currentPos[1]-70})) &&
				Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0], currentPos[1]-40}))
				){
			availableDirections +="U";
		}
		if(Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0] + 170, currentPos[1]})) &&
				Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0] + 70, currentPos[1]})) &&
				Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0] + 40, currentPos[1]}))
				){
			availableDirections +="R";
		}
		if(Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0], currentPos[1]+170})) &&
				Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0], currentPos[1]+70})) &&
				Arrays.equals(canvasDefaultColor, getCanvasColor(new int[]{currentPos[0], currentPos[1]+40}))
				){
			availableDirections +="D";
		}

		int randomMove = 0;
		if(availableDirections.length() != 0){
			randomMove = random.nextInt(availableDirections.length());
		}else{
			return new int[]{0,0};
		}

		char nextMoveDirection = availableDirections.charAt(randomMove);
		//System.out.println("Available:" + availableDirections);
		//System.out.println("MOVING : " + nextMoveDirection);
		//System.out.println("========");
		switch (nextMoveDirection) {
		case 'L':
			resultNextMove[0] = currentPos[0] - 150;
			resultNextMove[1] = currentPos[1];
			break;
		case 'U':
			resultNextMove[0] = currentPos[0];
			resultNextMove[1] = currentPos[1] - 150;
			break;
		case 'R':
			resultNextMove[0] = currentPos[0] + 150;
			resultNextMove[1] = currentPos[1];
			break;
		case 'D':
			resultNextMove[0] = currentPos[0];
			resultNextMove[1] = currentPos[1] + 150;
			break;
		}

		return resultNextMove;
	}
}
