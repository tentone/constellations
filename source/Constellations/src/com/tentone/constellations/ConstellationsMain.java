package com.tentone.constellations;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class ConstellationsMain implements ApplicationListener
{
	public final String NAME = "Constellations";
	public final String VERSION = "V0.0.1";
	
	//Rendering
	private ShapeRenderer shape;
	private SpriteBatch batch;
	
	//Font
	private BitmapFont font;
	
	//Camera
	private OrthographicCamera camera;
	
	//Selection control
	private boolean selecting;
	
	//Touch zoom and move
	private boolean moving;
	private float initial_zoom;
	private Vector2 initial_position;
	
	//Touch points
	private Vector2[] initial_point, actual_point;
	
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
		
		//Flags
		selecting = false;
		moving = false;
		initial_position = new Vector2(0, 0);
		
		//Points
		initial_point = new Vector2[2];
		actual_point = new Vector2[2];
		
		for(int i = 0; i < initial_point.length; i++)
		{
			initial_point[i] = new Vector2(0, 0);
			actual_point[i] = new Vector2(0, 0);
		}
		
		//Selected creature
		selected = new ArrayList<Creature>();
		
		//Touch handler
		touch = new Touch(camera);
		
		//Generate world
		world = World.generateWorld();
		
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

			//public boolean keyDown(int key){if(key==Input.Keys.PLUS){}}
			//public boolean keyUp(int key){}
		});
	}
	
	public void update()
	{
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
			if(Gdx.input.isKeyPressed(Input.Keys.UP))
			{
				camera.position.y += 0.1f;
				camera.update();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
			{
				camera.position.y -= 0.1f;
				camera.update();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			{
				camera.position.x += 0.1f;
				camera.update();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
			{
				camera.position.x -= 0.1f;
				camera.update();
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
			{
				Gdx.app.exit();
			}
		}
		
		//Dual touch events
		if(Gdx.input.isTouched(0) && Gdx.input.isTouched(1))
		{
			if(moving)
			{
				//Get actual point
				actual_point[0].set(touch.getPosition(0));
				actual_point[1].set(touch.getPosition(1));
				
				Vector2 delta = touch.getDelta(0).cpy().add(touch.getDelta(1));
				delta.scl(0.5f);
				
				//Move camera
				camera.position.x -= delta.x;
				camera.position.y += delta.y;

				//Zoom camera
				camera.zoom = initial_zoom - actual_point[0].dst(actual_point[1]) - initial_point[0].dst(initial_point[1]);

				//Update camera projection matrix
				camera.update();
			}
			else
			{
				//Store initial touch points
				initial_point[0].set(touch.getPosition(0));
				initial_point[1].set(touch.getPosition(1));
				
				//Store initial camera position and zoom
				initial_position.set(camera.position.x, camera.position.y);
				initial_zoom = camera.zoom;
				
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
			if(selecting)
			{
				actual_point[0].set(touch.getPosition(0));
			}
			else
			{	
				//Start selection
				selecting = true;
				initial_point[0].set(touch.getPosition(0));
				actual_point[0].set(initial_point[0]);
			}
		}
		else if(selecting)
		{
			//Reset selection flag
			selecting = false;
			
			//Calculate selection circle
			Circle selection = new Circle((initial_point[0].x + actual_point[0].x) / 2f, (initial_point[0].y + actual_point[0].y) / 2f, initial_point[0].dst(actual_point[0]) / 2f);
			
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
					creature.target.set(touch.getPosition(0));
					creature.task = Task.Move;
					
					//Check if clicked on some planet
					Iterator<Planet> itp = world.planets.iterator();
					while(itp.hasNext())
					{
						Planet planet = itp.next();
						if(planet.contains(creature.target))
						{
							if(!planet.conquered)
							{
								creature.task = Task.Conquer;
							}
							else if(planet.owner == creature.owner)
							{
								creature.task = Task.Upgrade;
							}
							
							break;
						}
					}
				}
				
				//Clear selection
				selected.clear();
			}
		}

		//Update world
		world.update(Gdx.graphics.getDeltaTime());	
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
		
		shape.setProjectionMatrix(camera.combined);
		shape.setAutoShapeType(true);
		shape.begin();

		//Draw planets
		Iterator<Planet> itp = world.planets.iterator();
		while(itp.hasNext())
		{
			Planet planet = itp.next();
			
			if(planet.owner == null)
			{
				shape.setColor(Color.GRAY);
			}
			else
			{
				shape.setColor(planet.owner.color);
			}

			if(planet.conquered)
			{
				shape.set(ShapeType.Filled);
				shape.circle(planet.x, planet.y, planet.level, 32);
				shape.rect(planet.x - 0.75f, planet.y - planet.level - 1f, 0.015f * planet.life / planet.level, 0.2f);
				
				shape.set(ShapeType.Line);
				for(int i = planet.level + 1; i <= planet.size; i++)
				{
					shape.circle(planet.x, planet.y, i, 32);
				}
				shape.rect(planet.x - 0.75f, planet.y - planet.level - 1f, 1.5f, 0.2f);
			}
			else
			{
				shape.set(ShapeType.Filled);
				shape.rect(planet.x - 0.75f, planet.y - planet.level - 1f, 0.015f * planet.life / planet.level, 0.2f);
				
				shape.set(ShapeType.Line);
				for(int i = planet.level; i <= planet.size; i++)
				{
					shape.circle(planet.x, planet.y, i, 32);
				}
				shape.rect(planet.x - 0.75f, planet.y - planet.level - 1f, 1.5f, 0.2f);
			}
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
		shape.setColor(0.8f, 0.8f, 0.8f, 1f);
		while(itc.hasNext())
		{
			Creature creature = itc.next();
			shape.circle(creature.x, creature.y, 0.1f, 4);
		}
		
		//Draw world border
		shape.set(ShapeType.Line);
		shape.setColor(1f, 1f, 0f, 1f);
		shape.rect(world.x, world.y, world.width, world.height);

		//If dragging draw area
		if(selecting)
		{
			shape.setColor(0f, 1f, 0f, 1f);
			shape.circle((initial_point[0].x + actual_point[0].x) / 2f, (initial_point[0].y + actual_point[0].y) / 2f, initial_point[0].dst(actual_point[0]) / 2f, 32);
		}
		
		//End shape renderer
		shape.end();
		
		//Draw overlay
		batch.begin();
		font.draw(batch, "Selected " + selected.size(), 5f, 120f);
		font.draw(batch, "Planets " + world.planets.size(), 5f, 100f);
		font.draw(batch, "Creatures " + world.creatures.size(), 5f, 80f);
		font.draw(batch, "FPS " + Gdx.graphics.getFramesPerSecond(), 5f, 60f);
		font.draw(batch, "Screen Mode " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), 5f, 40f);
		font.draw(batch, NAME + " " + VERSION, 5f, 20f);
		batch.end();
	}
	
	@Override
	public void resize(int width, int height)
	{
		camera.setAspectRatio((float)width/(float)height);
		camera.updateSizeRatio(width);
		camera.update();
	}

	@Override
	public void dispose()
	{
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
