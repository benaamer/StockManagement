package com.benaamer.stockmanagement.stockmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Runnable {

    public static final String OUTPUT_FILE = "expiryDatesLists.json";

    private NavigationView navigationView;

    public ListList myLists = new ListList();
    public ListList emptyList = new ListList();
    ExpiryDatesList emptyExpiryDatesList = new ExpiryDatesList(0,0);

    private boolean scannerWorked;
    private String scannerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emptyList.add(emptyExpiryDatesList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadListsFromFile();

        ListView currentListView = (ListView) findViewById(R.id.item_list);

        currentListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                popupDelete(myLists.getCurrent().getItem((int)id));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        writeListsToFile();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        for(int i = 0; i < myLists.size(); i++)
        {
            ExpiryDatesList the_list = myLists.get(i);
            if(id == the_list.getListID())
            {
                myLists.setCurrentList(i);
                renderList(myLists.getCurrent());
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void renderList(ExpiryDatesList list)
    {
        ListView currentListView = (ListView)findViewById(R.id.item_list);
        ListAdapter listAdapter = new ListAdapter(getApplicationContext(), list.getItems());
        currentListView.setAdapter(listAdapter);
        TextView text = (TextView)findViewById(R.id.Title);
        text.setText(myLists.getCurrent().getList_name());
    }

    private void addListToSidebar(ExpiryDatesList list) {
        Menu menu = navigationView.getMenu();
        menu.add(R.id.listMenu, list.getListID(), Menu.NONE, list.getList_name());
    }

    private void removeListFromSidebar(ExpiryDatesList list) {
        Menu menu = navigationView.getMenu();
        menu.removeItem(list.getListID());
    }


    public void openCamera()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    public void run() {
        Item newItem = new Item(this.resul.getContents());
        scannerWorked = true;
        scannerResult = newItem.getBarcode();
        if (!newItem.getName().equals("Unnamed item")) {
            myLists.getCurrent().addItem(newItem);
        } else {
            scannerWorked = false;
        }
        this.resul = null;
    }

    IntentResult resul;

    @Override
    public synchronized void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            this.resul = result;
            Thread thread = new Thread(this);
            thread.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (myLists.size() != 0) {
                renderList(myLists.getCurrent());
            }
            if (!scannerWorked) {
                boolean doIt = true;
                for (int i = 0; i < myLists.getAddedCodes().size(); i++) {
                    System.out.println(myLists.getAddedCodes().get(i));
                    Item code = myLists.getAddedCodes().get(i);
                    if (scannerResult.equals(code.getBarcode())) {
                        doIt = false;
                        popupInputNumber2(myLists.getAddedCodes().get(i).getName(), myLists.getAddedCodes().get(i).getExpiryDate());
                        break;
                    }
                }
                if (doIt) {
                    popupInputNumber2(null, null);
                }
                for (Item list: myLists.getAddedCodes()) {
                    System.out.println(list);
                }
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_list:
                if (myLists.size() > 0) {
                    removeListFromSidebar(myLists.getCurrent());
                    myLists.remove(myLists.getCurrent());
                    setHomeScreen();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadListsFromFile() {
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, OUTPUT_FILE);

        Gson gson = new Gson();
        try {
            FileReader in = new FileReader(file);
            myLists = gson.fromJson(in, ListList.class);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            for (int i = 0; i < myLists.getIterable().size(); i++) {
                addListToSidebar(myLists.get(i));
            }
        }
    }

    private void writeListsToFile() {
        Gson gson = new Gson();
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, OUTPUT_FILE);
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file);
            stream.write(gson.toJson(myLists).getBytes());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void popupDelete(final Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setTitle("Check item off this list?");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myLists.getCurrent().removeItem(item);
                renderList(myLists.getCurrent());
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void setHomeScreen() {
        TextView text = (TextView)findViewById(R.id.Title);
        text.setText("Home Screen");
        ListView currentListView = (ListView)findViewById(R.id.item_list);
        ListAdapter listAdapter = new ListAdapter(getApplicationContext(), emptyList.getCurrent().getItems());
        currentListView.setAdapter(listAdapter);
        myLists.setCurrentList(0);
    }

    public void popupInputNumber2(String name, String expiryDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText productText = new EditText(getApplicationContext());
        productText.setTextColor(Color.BLACK);
        productText.setHintTextColor(Color.GRAY);
        if (name != null) {
            productText.setText(name);
        } else {
            productText.setHint("Product");
        }
        layout.addView(productText);


        final EditText expiryDateText = new EditText(getApplicationContext());
        expiryDateText.setTextColor(Color.BLACK);
        expiryDateText.setHintTextColor(Color.GRAY);
        if (expiryDate != null){
            expiryDateText.setText(expiryDate);
        }else {
            expiryDateText.setHint("Expiry Date");
        }

        layout.addView(expiryDateText);

        builder.setView(layout);

        builder.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int listIndex = myLists.size();
                
                boolean created = true;
                for (int i = 0; i < myLists.size(); i++) {
                    if (myLists.get(i).getList_name().equals(expiryDateText.getText().toString())) {
                        created = false;
                        break;
                    }
                }

                if (created) {

                    int listID = View.generateViewId();

                    ExpiryDatesList list1 = new ExpiryDatesList(listIndex, listID);
                    myLists.add(list1);

                    myLists.getCurrent().setList_name(expiryDateText.getText().toString());
                    addListToSidebar(myLists.getCurrent());

                    Item item = new Item(productText.getText().toString(), expiryDateText.getText().toString(), scannerResult);

                    myLists.getCurrent().addItem(item);
                    myLists.addNewCode(item);

                    dialog.cancel();
                    renderList(myLists.getCurrent());

                } else {
                    Item item = new Item(productText.getText().toString(), expiryDateText.getText().toString(), scannerResult);

                    for (int i = 0; i < myLists.size(); i++) {
                        if (myLists.get(i).getList_name().equals(expiryDateText.getText().toString())) {
                            myLists.get(i).addItem(item);
                            break;
                        }
                    }

                    myLists.addNewCode(item);

                    dialog.cancel();
                    renderList(myLists.getCurrent());
                }
            }
        });

        builder.show();
    }

}
