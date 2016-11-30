package com.tentone.constellations;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.camera.OrthographicCamera;
import com.tentone.constellations.elements.Creature;
import com.tentone.constellations.elements.Planet;
import com.tentone.constellations.elements.World;
import com.tentone.constellations.input.Touch;

public class ConstellationsMain implements ApplicationListener
{
	private ShapeRenderer shape_renderer;
	private OrthographicCamera camera;
	
	private World world;
	
	private Touch touch;

	@Override
	public void create()
	{
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		
		//Shape renderer
		shape_renderer = new ShapeRenderer();
		
		//Camera
		camera = new OrthographicCamera(1, width / height, width);
		camera.zoom = 20f;
		
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
				camera.zoom = camera.zoom + amount * camera.zoom * 0.25f;
				if(camera.zoom < 0.5f)
				{
					camera.zoom = 0.5f;
				}
				camera.update();
				return false;
			}
			
			//Key Pressed Down
			//public boolean keyDown(int key){if(key==Input.Keys.PLUS){}}
		});
	}
	
	@Override
	public void render()
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
		
		
		//Dual touch events
		if(Gdx.input.isTouched(0) && Gdx.input.isTouched(1))
		{
			Vector2 a = touch.getDelta(0).cpy();
			Vector2 b = touch.getDelta(1);
			float dot = a.dot(b);
			
			//Move camera
			camera.position.x -= (a.x + b.x) / 2f;
			camera.position.y += (a.y + b.y) / 2f;

			//Distance between points
			float dist = a.dst(b);
			
			//Zoom camera
			camera.zoom += dist;
			
			//Update camera projection matrix
			camera.update();
		}
		
		//Select creatures
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT))
		{
			//TODO <ADD CODE HERE>
		}
		
		//Send command to selected creatures
		//TODO <ADD CODE HERE>

		world.update(Gdx.graphics.getDeltaTime());

		//Render stuff to screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shape_renderer.setProjectionMatrix(camera.combined);
		shape_renderer.setAutoShapeType(true);
		shape_renderer.begin();

		//Draw planets
		Iterator<Planet> itp = world.planets.iterator();
		while(itp.hasNext())
		{
			Planet planet = itp.next();
			
			if(planet.owner == null)
			{
				shape_renderer.setColor(0.4f, 0.4f, 0.4f, 1);
			}
			else
			{
				shape_renderer.setColor(planet.owner.color);
			}

			shape_renderer.set(ShapeType.Filled);
			shape_renderer.circle(planet.x, planet.y, planet.level, 32);
			
			for(int i = planet.level; i <= planet.size; i++)
			{
				shape_renderer.set(ShapeType.Line);
				shape_renderer.circle(planet.x, planet.y, i, 32);
			}
		}
				
		shape_renderer.set(ShapeType.Filled);
		
		//Draw creatures
		Iterator<Creature> itc = world.creatures.iterator();
		while(itc.hasNext())
		{
			Creature creature = itc.next();
			
			if(creature.owner == null)
			{
				shape_renderer.setColor(0.8f, 0.8f, 0.8f, 1);
			}
			else
			{
				shape_renderer.setColor(creature.owner.color);
			}
			
			
			shape_renderer.circle(creature.x, creature.y, 0.05f, 4);
		}
		
		//Draw world border
		shape_renderer.set(ShapeType.Line);
		shape_renderer.setColor(1f, 1f, 0f, 1);
		shape_renderer.rect(world.x, world.y, world.width, world.height);

		shape_renderer.end();	 
	}

	@Override
	public void resize(int width, int height)
	{
		camera.setAspectRatio((float)width/(float)height);
		camera.update();
	}

	@Override
	public void dispose()
	{
		shape_renderer.dispose();
	}
	
	@Override
	public void pause(){}

	@Override
	public void resume(){}
}
