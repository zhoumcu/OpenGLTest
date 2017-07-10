package org.andresoviedo.app.model3D.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.services.ExampleSceneLoader;
import org.andresoviedo.app.model3D.services.SceneLoader;
import org.andresoviedo.app.util.Utils;
import org.andresoviedo.dddmodel2.R;

import java.io.File;
import java.util.ArrayList;

/**
 * This activity represents the container for our 3D viewer.
 * 
 * @author andresoviedo
 */
public class ModelActivity extends Activity implements View.OnClickListener {

	private String paramAssetDir;
	private String paramAssetFilename;
	/**
	 * The file to load. Passed as input parameter
	 */
	private String paramFilename;
	/**
	 * Enter into Android Immersive mode so the renderer is full screen or not
	 */
	private boolean immersiveMode = true;
	/**
	 * Background GL clear color. Default is light gray
	 */
	private float[] backgroundColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};

	private ModelSurfaceView gLView;

	private SceneLoader scene;

	private Handler handler;

	public String[] models = new String[]{"bei","bukuai1","bukuai2"/*,"gupen","gupennew"*/};
	private Object3DData selectedObject;
    private boolean isHideChoose ;
    private boolean isTransparentChoose;
    private boolean isTransparentOtherChoose;
    private boolean isHideOhterChoose;
	private TextView hideChoose;
	private TextView transparentChoose;
	private TextView hideOtherChoose;
	private TextView transparentOtherChoose;
	private TextView changeChooseColor;
	private TextView undoChooseColor;
	private LinearLayout point;
	private LinearLayout line;
	private LinearLayout face;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Try to get input parameters
		Bundle b = getIntent().getExtras();
		if (b != null) {
			this.paramAssetDir = b.getString("assetDir");
			this.paramAssetFilename = b.getString("assetFilename");
			this.paramFilename = b.getString("uri");
			this.immersiveMode = "true".equalsIgnoreCase(b.getString("immersiveMode"));
			try{
				String[] backgroundColors = b.getString("backgroundColor").split(" ");
				backgroundColor[0] = Float.parseFloat(backgroundColors[0]);
				backgroundColor[1] = Float.parseFloat(backgroundColors[1]);
				backgroundColor[2] = Float.parseFloat(backgroundColors[2]);
				backgroundColor[3] = Float.parseFloat(backgroundColors[3]);
			}catch(Exception ex){
				// Assuming default background color
			}
		}
		Log.i("Renderer", "Params: assetDir '" + paramAssetDir + "', assetFilename '" + paramAssetFilename + "', uri '"
				+ paramFilename + "'");

		handler = new Handler(getMainLooper());

		setContentView(R.layout.activity_test);
		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		gLView = new ModelSurfaceView(this);
		((RelativeLayout) this.findViewById(R.id.main_view_content))
				.addView(gLView);
//		setContentView(gLView);

		initList();
		initView();
		// Create our 3D sceneario
		if (paramFilename == null && paramAssetFilename == null) {
			scene = new ExampleSceneLoader(this);
		} else {
			scene = new SceneLoader(this);
		}
//		scene.init();
		// Show the Up button in the action bar.
		setupActionBar();

		// TODO: Alert user when there is no multitouch support (2 fingers). He won't be able to rotate or zoom for
		// example
		Utils.printTouchCapabilities(getPackageManager());

		setupOnSystemVisibilityChangeListener();

		this.findViewById(R.id.sview_titlebar_setting).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
//				toolPressed(v);
				LinearLayout layout = new LinearLayout(ModelActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);

				final TextView colorText = new TextView(ModelActivity.this);
				ColorPickerView colorPick = new ColorPickerView(ModelActivity.this, Color.parseColor("#FFFFFF"), 0.8,colorText);

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
				lp.gravity = Gravity.CENTER_HORIZONTAL;
				layout.addView(colorPick, lp);
				layout.addView(colorText,lp);

				AlertDialog mAlertDialog = new AlertDialog.Builder(ModelActivity.this)
						.setTitle("选择背景颜色")
						.setView(layout)
						.setPositiveButton(getString(R.string.dialog_color_OK), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String c = colorText.getText().toString().substring(1);
								float r = Integer.parseInt(c.substring(0,2),16)/255.0f;
								float g = Integer.parseInt(c.substring(2,4),16)/255.0f;
								float b = Integer.parseInt(c.substring(4, 6),16)/255.0f;
								ModelRenderer.br = r;
								ModelRenderer.bg  = g;
								ModelRenderer.bb  = b;
								gLView.requestRender();
							}
						})
						.setNegativeButton(getString(R.string.dialog_color_cancle), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).show();
			}});

		for (String model : models){
			loadDemo(model);
		}
	}

	private void initView() {
		hideChoose = (TextView)findViewById(R.id.hide_choose);
		transparentChoose = (TextView)findViewById(R.id.transparent_choose);
		hideOtherChoose = (TextView)findViewById(R.id.hide_other);
		transparentOtherChoose = (TextView)findViewById(R.id.transparent_other);
		changeChooseColor = (TextView)findViewById(R.id.change_choose_color);
		undoChooseColor = (TextView)findViewById(R.id.undo_choose_color);
		point = (LinearLayout)findViewById(R.id.sview_displaydialog_point);
		line = (LinearLayout)findViewById(R.id.sview_displaydialog_line);
		face = (LinearLayout)findViewById(R.id.sview_displaydialog_face);
		hideChoose.setOnClickListener(this);
		transparentChoose.setOnClickListener(this);
		hideOtherChoose.setOnClickListener(this);
		transparentOtherChoose.setOnClickListener(this);
		changeChooseColor.setOnClickListener(this);
		undoChooseColor.setOnClickListener(this);
		point.setOnClickListener(this);
		line.setOnClickListener(this);
		face.setOnClickListener(this);
	}

	private void initList(){
//		AssetManager assets = getApplicationContext().getAssets();

//		try {
//			models = assets.list("models");
//		} catch (IOException ex) {
//			Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
//			return;
//		}

		// add 1 entry per model found
		final ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
		for (String model : models) {
//			if (model.toLowerCase().endsWith(".obj") || model.toLowerCase().endsWith(".stl")) {
				RowItem item = new RowItem("models/" + model, model, "models/" + model + ".jpg");
				rowItems.add(item);
//			}
		}
		ListView listView = (ListView) findViewById(R.id.listview);

		StringAdapter adapter = new StringAdapter(this,rowItems);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final RowItem selectedItem = (RowItem) rowItems.get(position);
				selectedObject = scene.setSelectedObjectByID(selectedItem.path);
				findViewById(R.id.main_bottom_toolbar).setVisibility(View.VISIBLE);
			}
		});
	}
	private void loadDemo(final String selectedItem) {
		this.paramAssetDir = "models";
		this.paramAssetFilename = selectedItem+".stl";
		this.immersiveMode = true;
		scene.init();
	}
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.model, menu);
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupOnSystemVisibilityChangeListener() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				// Note that system bars will only be "visible" if none of the
				// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
					// TODO: The system bars are visible. Make any desired
					// adjustments to your UI, such as showing the action bar or
					// other navigational controls.
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						hideSystemUIDelayed(3000);
					}
				} else {
					// TODO: The system bars are NOT visible. Make any desired
					// adjustments to your UI, such as hiding the action bar or
					// other navigational controls.
				}
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				if (immersiveMode) hideSystemUIDelayed(5000);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.model_toggle_wireframe:
			scene.toggleWireframe();
			break;
		case R.id.model_toggle_boundingbox:
			scene.toggleBoundingBox();
			break;
		case R.id.model_toggle_textures:
			scene.toggleTextures();
			break;
		case R.id.model_toggle_lights:
			scene.toggleLighting();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void hideSystemUIDelayed(long millis) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		handler.postDelayed(new Runnable() {
			public void run() {
				hideSystemUI();
			}
		}, millis);
	}

	private void hideSystemUI() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			hideSystemUIKitKat();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			hideSystemUIJellyBean();
		}
	}

	// This snippet hides the system bars.
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void hideSystemUIKitKat() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		// Set the IMMERSIVE flag.
		// Set the content to appear under the system bars so that the content
		// doesn't resize when the system bars hide and show.
		final View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
				| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
				| View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void hideSystemUIJellyBean() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			return;
		}
		final View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}

	// This snippet shows the system bars. It does this by removing all the flags
	// except for the ones that make the content appear under the system bars.
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void showSystemUI() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			return;
		}
		final View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	public File getParamFile() {
		return getParamFilename() != null ? new File(getParamFilename()) : null;
	}

	public String getParamAssetDir() {
		return paramAssetDir;
	}

	public String getParamAssetFilename() {
		return paramAssetFilename;
	}

	public String getParamFilename() {
		return paramFilename;
	}

	public float[] getBackgroundColor(){
		return backgroundColor;
	}

	public SceneLoader getScene() {
		return scene;
	}

	public GLSurfaceView getgLView() {
		return gLView;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.hide_choose:
				isHideChoose = !isHideChoose;
				if(isHideChoose){
					scene.hideChooseObject(selectedObject);
					transparentChoose.setEnabled(false);
					hideChoose.setSelected(true);
                    hideChoose.setText("显示所选");
				}else {
					scene.noHideChooseObject(selectedObject);
					transparentChoose.setEnabled(true);
					hideChoose.setSelected(false);
                    hideChoose.setText("隐藏所选");
				}
				break;
			case R.id.transparent_choose:
				isTransparentChoose = !isTransparentChoose;
				if(isTransparentChoose){
                    transparentChoose.setText("恢复所选");
                    transparentChoose.setSelected(true);
					scene.transparentChooseObject(selectedObject);
				}else {
                    transparentChoose.setText("透明所选");
                    transparentChoose.setSelected(false);
					scene.noTransparentChooseObject(selectedObject,SceneLoader.DEFAULT_COLOR);
				}
				break;
			case R.id.hide_other:
				isHideOhterChoose = !isHideOhterChoose;
				if(isHideOhterChoose){
                    transparentOtherChoose.setEnabled(false);
                    hideOtherChoose.setSelected(true);
                    hideOtherChoose.setText("显示其他");
					scene.hideOtherChooseObject(selectedObject);
				}else {
                    transparentOtherChoose.setEnabled(true);
                    hideOtherChoose.setSelected(false);
                    hideOtherChoose.setText("隐藏其他");
					scene.noHideOtherChooseObject(selectedObject);
				}
				break;
			case R.id.transparent_other:
				isTransparentOtherChoose = !isTransparentOtherChoose;
				if(isTransparentOtherChoose){
                    transparentOtherChoose.setText("恢复所选");
                    transparentOtherChoose.setSelected(true);
					scene.transparentOtherChooseObject(selectedObject);
				}else {
                    transparentOtherChoose.setText("透明所选");
                    transparentOtherChoose.setSelected(false);
					scene.noTransparentOtherChooseObject(selectedObject,SceneLoader.DEFAULT_COLOR);
				}
				break;
			case R.id.change_choose_color:
				LinearLayout layout = new LinearLayout(ModelActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);

				final TextView colorText = new TextView(ModelActivity.this);
				ColorPickerView colorPick = new ColorPickerView(ModelActivity.this, Color.parseColor("#FFFFFF"), 0.8,colorText);

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
				lp.gravity = Gravity.CENTER_HORIZONTAL;
				layout.addView(colorPick, lp);
				layout.addView(colorText,lp);
				AlertDialog mAlertDialog = new AlertDialog.Builder(ModelActivity.this)
						.setTitle("选择背景颜色")
						.setView(layout)
						.setPositiveButton(getString(R.string.dialog_color_OK), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String c = colorText.getText().toString().substring(1);
								float r = Integer.parseInt(c.substring(0,2),16)/255.0f;
								float g = Integer.parseInt(c.substring(2,4),16)/255.0f;
								float b = Integer.parseInt(c.substring(4, 6),16)/255.0f;
								scene.setSelectedObjectColor(selectedObject,new float[]{r,g,b,1.0f});
							}
						})
						.setNegativeButton(getString(R.string.dialog_color_cancle), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).show();
				break;
			case R.id.undo_choose_color:
				scene.setSelectedObjectColor(selectedObject,SceneLoader.DEFAULT_COLOR);
				break;
			case R.id.sview_displaydialog_point:
				scene.setPointWireframe();
				break;
			case R.id.sview_displaydialog_line:
				scene.setLineWireframe();
				break;
			case R.id.sview_displaydialog_face:
				scene.setFaceWireframe();
				break;
		}
	}
}
