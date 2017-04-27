package lite.organizer.mtg.harlequin.com.hedronalignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

import static android.R.id.tabs;

public class UserPage extends AppCompatActivity {


    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private FloatingActionButton fab;
    private Toolbar main_toolbar;
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;
    private DatabaseReference mDatabaseReference;
    private CoordinatorLayout mCoordinatorLayout;
    private TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);

        Typeface steinerLight = Typeface.createFromAsset(getAssets(),"fonts/Hero.otf");


        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_content);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //per cambiare il font nel tablayout
        ViewGroup vg = (ViewGroup) tabs.getChildAt(0);
        changeFontInViewGroup(vg,"fonts/Holo.otf");


        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager mStaggeredGridLayoutManagerVertical = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManagerVertical);//staggered vertical default

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.vector_burger_menu_24));

        final TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setTypeface(steinerLight);
        toolbarTitle.setText("Tagli Uomo");



        fab = (FloatingActionButton)findViewById(R.id.fab);

        mProgressDialog = new ProgressDialog(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createEvent = new Intent(UserPage.this,CreateEvent.class);
                startActivity(createEvent);
            }
        });


    }//Fine OnCreate

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new TagliFragment(), "Tagli");
        adapter.addFragment(new IserniaFragment(), "Isernia");
        adapter.addFragment(new CarpinoneFragment(), "Carpinone");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    void changeFontInViewGroup(ViewGroup viewGroup, String fontPath) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (TextView.class.isAssignableFrom(child.getClass())) {

                CalligraphyUtils.applyFontToTextView(child.getContext(), (TextView) child, fontPath);
            } else if (ViewGroup.class.isAssignableFrom(child.getClass())) {
                changeFontInViewGroup((ViewGroup) viewGroup.getChildAt(i), fontPath);
            }
        }
    }

    public static class CutViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView cutName;
        ImageView cutImage;

        public CutViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            cutName = (TextView)mView.findViewById(R.id.cut_name);
            cutImage= (ImageView) mView.findViewById(R.id.cut_pic);

        }

        //setta il nome in base al taglio
        public void setCutName (String name){
            cutName.setText(name);
        }

        //carica L'immagine nella card
        public void setCutImage (final String imagePath, final Context ctx){
            Picasso.with(ctx)
                   .load(imagePath)
                   .networkPolicy(NetworkPolicy.OFFLINE)
                   .into(cutImage, new Callback() {
                       @Override
                       public void onSuccess() {
                           //tutto ok
                       }

                       @Override
                       public void onError() {
                           Picasso.with(ctx).load(imagePath).into(cutImage);
                       }
                   });
        }
    }//fine ViewHolder

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Cut,CutViewHolder>(
                Cut.class,
                R.layout.card,
                CutViewHolder.class,
                mDatabaseReference.child("Cuts")
        ) {
            @Override
            protected void populateViewHolder(CutViewHolder viewHolder, Cut model, final int position) {

                viewHolder.setCutImage(model.getImagePath(),getApplicationContext());
                viewHolder.setCutName(model.getCutName());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(UserPage.this,"Hai premuto l'elemento "+position,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Opzioni del Menu nella toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            // action with ID action_refresh was selected
            case R.id.logout_btn:
                mProgressDialog.setMessage("LogOut...");
                mProgressDialog.show();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent toStart = new Intent(UserPage.this,RegistrationActivity.class);
                startActivity(toStart);
                mProgressDialog.dismiss();
                break;

            case R.id.linearViewVertical:
                LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
                mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(mLinearLayoutManagerVertical);
                break;

            case R.id.linearViewHorizontal:
                LinearLayoutManager mLinearLayoutManagerHorizontal = new LinearLayoutManager(this);
                mLinearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRecyclerView.setLayoutManager(mLinearLayoutManagerHorizontal);
                break;

            case R.id.gridView:
                GridLayoutManager mGridLayoutManager = new GridLayoutManager(this,2);
                mRecyclerView.setLayoutManager(mGridLayoutManager);
                break;

            case R.id.staggeredViewVertical:
                StaggeredGridLayoutManager mStaggeredGridLayoutManagerVertical = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(mStaggeredGridLayoutManagerVertical);
                break;

            case R.id.staggeredViewHorizontal:
                StaggeredGridLayoutManager mStaggeredGridLayoutManagerHorizontal = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL);
                mRecyclerView.setLayoutManager(mStaggeredGridLayoutManagerHorizontal);
                break;


            default:
                break;
        }

        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}


