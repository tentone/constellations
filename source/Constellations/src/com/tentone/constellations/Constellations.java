package com.tentone.constellations;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.camera.OrthographicCamera;
import com.tentone.constellations.elements.Creature;
import com.tentone.constellations.elements.Planet;
import com.tentone.constellations.elements.Task;
import com.tentone.constellations.elements.World;
import com.tentone.constellations.input.Touch;

public class Constellations implements ApplicationListener
{
	public static final String NAME = "Constellations";
	public static final String VERSION = "V0.0.1";
	public static final String TIMESTAMP = "201612081110";
	
	//Debug flags
	private boolean debug_quad_tree = false;
	private boolean debug_overlay = true;
	
	//Performance log
	private ArrayList<String> log = new ArrayList<String>();
	private float log_time;
	
	//Rendering
	private ShapeRenderer shape;
	private SpriteBatch batch;
	
	//Font
	private BitmapFont font;
	
	//Camera
	private OrthographicCamera camera, overlay;
	
	//Selection and move control flags
	private boolean selecting, moving;
	private Vector2 initial_point;
	private Circle selection;
	
	//Selected creatures
	private ArrayList<Creature> selected;
	
	//World
	private World world;
	
	//Touch handler
	private Touch touch;
	
	@Override
	public void create()
	{
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		
		//Rendering
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		//Font
		font = new BitmapFont();
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		//Camera
		camera = new OrthographicCamera(1, width / height, width);
		camera.zoom = 20f;
		
		overlay = new OrthographicCamera(500, width / height, width);
		
		//Flags
		selecting = false;
		moving = false;
		initial_point = new Vector2(0, 0);
		
		//Selected creature
		selected = new ArrayList<Creature>();
		selection = new Circle();
		
		//Touch handler
		touch = new Touch(camera);
		
		//Generate world
		world = World.generateWorld(50, 30);
		
		//Input processor to handle mouse scrolling
		Gdx.input.setInputProcessor(new InputAdapter()
		{
			//Mouse scrolled
			public boolean scrolled(int amount)
			{
				camera.zoom += amount * camera.zoom * 0.25f;
				
				if(camera.zoom < 0.5f)
				{
					camera.zoom = 0.5f;
				}
				else if(camera.zoom > world.width)
				{
					camera.zoom = world.width;
				}
				
				camera.update();
				return false;
			}
		});
	}
	
	public void update()
	{
		touch.update();
		
		//Move camera with mouse
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
		{
			Vector2 delta = touch.getDelta(0);
			camera.position.x -= delta.x;
			camera.position.y += delta.y;
			camera.update();
		}
		
		//Move camera using keys
		if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY))
		{
			//Exit the game
			if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
			{
				Gdx.app.exit();
			}
			
			//Export log data as a CSV
			if(Gdx.input.isKeyJustPressed(Keys.L))
			{
				exportLog("out.csv");
			}
			
			//Toggle quad tree debug view
			if(Gdx.input.isKeyJustPressed(Keys.D))
			{
				debug_quad_tree = !debug_quad_tree;
			}
			
			//Toggle debug overlay
			if(Gdx.input.isKeyJustPressed(Keys.O))
			{
				debug_overlay = !debug_overlay;
			}
		}
		
		//Dual touch events
		if(Gdx.input.isTouched(0) && Gdx.input.isTouched(1))
		{
			if(moving)
			{
				//Move camera
				camera.position.x -= (touch.getDelta(0).x + touch.getDelta(1).x) / 2.0f;
				camera.position.y += (touch.getDelta(0).y + touch.getDelta(1).y) / 2.0f;

				//Zoom camera
				Vector2 last_a = touch.getPosition(0).cpy().sub(touch.getDelta(0));
				Vector2 last_b = touch.getPosition(1).cpy().sub(touch.getDelta(1));
				
				camera.zoom -= touch.getPosition(0).dst(touch.getPosition(1)) - last_a.dst(last_b);
				
				//Update camera projection matrix
				camera.update();
			}
			else
			{				
				//Set moving flag
				moving = true;
			}
			
			//Stop dragging
			selecting = false;
		}
		else
		{
			//Stop moving
			moving = false;
		}
		
		//Select creatures
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT))
		{
			if(!selecting)
			{
				initial_point.set(touch.getPosition(0));
				selection.set(0, 0, 0);
				selecting = true;
			}
			else
			{
				selection.set((initial_point.x + touch.getPosition(0).x) / 2f, (initial_point.y + touch.getPosition(0).y) / 2f, initial_point.dst(touch.getPosition(0)) / 2f);
			}
		}
		else if(selecting)
		{
			//Reset selection flag
			selecting = false;
			
			//Calculate selection circle
			selection.set((initial_point.x + touch.getPosition(0).x) / 2f, (initial_point.y + touch.getPosition(0).y) / 2f, initial_point.dst(touch.getPosition(0)) / 2f);
			
			//Only take action if selection radius is considerable
			if(selection.radius > 0.1f)
			{
				//Clear previous selection
				selected.clear();
				
				//Select creatures
				Iterator<Creature> creatures = world.creatures.iterator();
				while(creatures.hasNext())
				{
					Creature creature = creatures.next();
					if(selection.contains(creature))
					{
						selected.add(creature);
					}
				}
			}
			//Send command to selected creatures if any selected
			else if(selected.size() > 0)
			{
				//Move creatures
				Iterator<Creature> creatures = selected.iterator();
				while(creatures.hasNext())
				{
					Creature creature = creatures.next();
					creature.destination.set(touch.getPosition(0));
					creature.task = Task.Move;
					
					//Check if clicked on some planet
					Iterator<Planet> itp = world.planets.iterator();
					while(itp.hasNext())
					{
						Planet planet = itp.next();
						if(planet.contains(creature.destination))
						{
							creature.target = planet;
							
							if(!planet.conquered)
							{
								creature.task = Task.Conquer;
								creature.limit = 1;
							}
							else if(planet.owner == creature.owner)
							{
								if(planet.damaged())
								{
									creature.task = Task.Heal;
									creature.limit = planet.level;
								}
								else if(planet.upgradable())
								{
									creature.task = Task.Upgrade;
									creature.limit = planet.level + 1;
								}
							}
							
							break;
						}
					}
				}
				
				//Clear selection
				selected.clear();
			}
		}

		float delta = Gdx.graphics.getDeltaTime();
		
		//Update world
		world.update(delta);	
		
		//Update performance log
		log_time += delta;
		
		if(log_time > 0.5f)
		{
			log_time = 0f;
			log.add(world.creatures.size() + "|" + Gdx.graphics.getDeltaTime());
		}
	}
	
	@Override
	public void render()
	{
		update();
		
		//Render stuff to screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
		
		//Prepare shape renderer
		shape.setProjectionMatrix(camera.combined);
		shape.setAutoShapeType(true);
		shape.begin();

		//Draw planets
		Iterator<Planet> itp = world.planets.iterator();
		while(itp.hasNext())
		{
			Planet planet = itp.next();

			shape.setColor((planet.owner == null) ? Color.GRAY : planet.owner.color);
			shape.set(ShapeType.Filled);
			shape.circle(planet.x, planet.y, planet.life / (float) Planet.life_per_level, 32);
			shape.rect(planet.x - 0.75f, planet.y - planet.level - 1f, 0.015f * (planet.life % Planet.life_per_level), 0.2f);
			
			shape.set(ShapeType.Line);
			for(int i = planet.level; i <= planet.size; i++)
			{
				shape.circle(planet.x, planet.y, i, 32);
			}
			shape.rect(planet.x - 0.75f, planet.y - planet.level - 1f, 1.5f, 0.2f);
		}
				
		shape.set(ShapeType.Filled);
		
		//Draw creatures
		Iterator<Creature> itc = world.creatures.iterator();
		while(itc.hasNext())
		{
			Creature creature = itc.next();
			
			shape.setColor(creature.owner != null ? creature.owner.color : Color.GRAY);
			shape.circle(creature.x, creature.y, 0.1f, 4);
		}
		
		//Highlight selected creatures
		itc = selected.iterator();
		shape.setColor(0.6f, 0.6f, 0.6f, 1f);
		while(itc.hasNext())
		{
			Creature creature = itc.next();
			shape.circle(creature.x, creature.y, 0.15f, 4);
		}
		
		//Set draw mode to line
		shape.set(ShapeType.Line);

		//If dragging draw area
		if(selecting)
		{
			shape.setColor(0f, 1f, 0f, 1f);
			shape.circle(selection.x, selection.y, selection.radius, 32);
		}
		
		//Debug the quad tree
		if(debug_quad_tree)
		{
			shape.setColor(0.0f, 0.1f, 0.0f, 1f);
			world.tree.debug(shape);
		}
		
		//End shape renderer
		shape.end();
		
		//Debug overlay
		if(debug_overlay)
		{
			batch.setProjectionMatrix(overlay.combined);
			batch.begin();
			font.draw(batch, "Quad-Tree", 5f, 180f);
			font.draw(batch, "Consistent " + (world.tree.size() == world.creatures.size()), 5f, 160f);
			font.draw(batch, "Quad-Tree " + world.tree.size(), 5f, 140f);
			font.draw(batch, "Linked-Queue " + world.creatures.size(), 5f, 120f);
			
			font.draw(batch, "Selected " + selected.size(), 5f, 100f);
			font.draw(batch, "Planets " + world.planets.size(), 5f, 80f);
			font.draw(batch, "FPS " + Gdx.graphics.getFramesPerSecond(), 5f, 60f);
			font.draw(batch, "Screen Mode " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), 5f, 40f);
			font.draw(batch, NAME + " " + VERSION + "(" + TIMESTAMP + ")", 5f, 20f);
			batch.end();
		}
	}
	
	//Export log as CSV
	public void exportLog(String fname)
	{
		try
		{
			File file = new File(fname);
			PrintWriter pw = new PrintWriter(file);
			pw.println("sep=|");
			pw.println("creatures,delta");
			
			Iterator<String> it = this.log.iterator();
			while(it.hasNext())
			{
				pw.println(it.next().replace('.', ','));
			}
			
			pw.close();
		}
		catch(Exception e){}
	}
	
	@Override
	public void resize(int width, int height)
	{
		camera.setAspectRatio((float)width/(float)height);
		camera.updateSizeRatio(width);
		camera.update();
		
		overlay.setAspectRatio((float)width/(float)height);
		overlay.centerCamera();
	}
	
	@Override
	public void dispose()
	{
		
		font.dispose();
		batch.dispose();
		shape.dispose();
	}
	
	@Override
	public void pause(){}

	@Override
	public void resume()
	{
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
