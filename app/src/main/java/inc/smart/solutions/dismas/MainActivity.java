package inc.smart.solutions.dismas;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.BuildConfig;
import inc.smart.solutions.dismas.adapter.GridViewAdapter;
import inc.smart.solutions.dismas.adapter.ScreenSlidePagerAdapter;
import inc.smart.solutions.dismas.animations.ZoomOutPageTransformer;
import inc.smart.solutions.dismas.constants.Configs;
import inc.smart.solutions.dismas.databinding.ActivityMainBinding;
import inc.smart.solutions.dismas.dialogs.CustomDialog;
import inc.smart.solutions.dismas.models.GitHub;
import inc.smart.solutions.dismas.models.Projects;
import inc.smart.solutions.dismas.retrofit.GitHubApiManager;
import inc.smart.solutions.dismas.utils.DateUtils;
import inc.smart.solutions.dismas.utils.ExpandableHeightGridView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 99;
    private ArrayList<Projects> projects;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter screenSlidePagerAdapter;
    private int scrolledPagePosition = -1;
    private int clickedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // fetchUserGitHub();

        LinearLayout llLinkedIn = findViewById(R.id.llLinkedIn);
        LinearLayout llGitHub = findViewById(R.id.llGitHub);
        LinearLayout llWebsite = findViewById(R.id.llWebsite);
        LinearLayout llBlog = findViewById(R.id.llBlog);
        llLinkedIn.setOnClickListener(this);
        llGitHub.setOnClickListener(this);
        llWebsite.setOnClickListener(this);
        llBlog.setOnClickListener(this);

        ExpandableHeightGridView gridView = findViewById(R.id.gridView);
        projects = new ArrayList<>();

        getNotableProjects();

        viewPager = findViewById(R.id.viewPager);

        GridViewAdapter gridViewAdapter = new GridViewAdapter(MainActivity.this, projects);
        gridView.setExpanded(true);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                clickedPosition = position;
                viewPager.setVisibility(View.VISIBLE);
                setUpPagerAdapter(projects.get(position).getProjectScreenshots());
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int devWidth = displayMetrics.widthPixels;

        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(-devWidth / 2);

        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        binding.consoleTextView.setText(String.format(getString(R.string.last_visit_s_on_console), DateUtils.formatDayOfWeekMonthDayYearTime(new Date())));

        binding.inputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) { // EditorInfo.IME_ACTION_DONE = 6
                handleCommand();
                return true;
            }
            return false;
        });

        binding.inputEditText.setText(String.format("%s%s", getString(R.string.terminal_start), getString(R.string.ls_menu)));
        binding.inputEditText.setSelection(binding.inputEditText.getText().length());
        binding.inputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);

        binding.btnAbout.setOnClickListener(this);
        binding.btnExperience.setOnClickListener(this);
        binding.btnPortfolio.setOnClickListener(this);
        binding.btnSayHi.setOnClickListener(this);
        binding.btnClear.setOnClickListener(this);
    }

    private void transformImage(){
        // set gray scale to imageView
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        binding.imageView.setColorFilter(filter);

        // Use ViewTreeObserver to wait for the layout to be measured
        ViewTreeObserver viewTreeObserver = binding.imageView.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Remove the listener to avoid multiple callbacks
                binding.imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                // Get the width of the ImageView
                int imageViewWidth = binding.imageView.getWidth();

                // Calculate the translation value (50% of the width)
                int translationX = imageViewWidth / 2;

                // Apply translation to the ImageView
                binding.imageView.setTranslationX(-translationX);

                // Return true to continue with the drawing
                return true;
            }
        });
    }
    private void aboutAnimation(){
        /** //Create a scaling animation
         Animation animation = new ScaleAnimation(0, 1,  // fromXScale, toXScale
         0, 1,  // fromYScale, toYScale
         Animation.RELATIVE_TO_SELF, 0.5f,  // pivotX
         Animation.RELATIVE_TO_SELF, 0.5f   // pivotY
         );
         animation.setDuration(1000);
         animation.setInterpolator(new AccelerateDecelerateInterpolator());*/

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnExperience.setVisibility(View.VISIBLE);
                experienceAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnAbout.startAnimation(animation);
    }

    private void reverseAboutAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reverse_button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnAbout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnAbout.startAnimation(animation);
    }

    private void experienceAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnPortfolio.setVisibility(View.VISIBLE);
                portfolioAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnExperience.startAnimation(animation);
    }

    private void reverseExperienceAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reverse_button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnExperience.setVisibility(View.GONE);
                reverseAboutAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnExperience.startAnimation(animation);
    }

    private void portfolioAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnSayHi.setVisibility(View.VISIBLE);
                sayHiAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnPortfolio.startAnimation(animation);
    }

    private void reversePortfolioAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reverse_button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnPortfolio.setVisibility(View.GONE);
                reverseExperienceAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnPortfolio.startAnimation(animation);
    }

    private void sayHiAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnClear.setVisibility(View.VISIBLE);
                clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnSayHi.startAnimation(animation);
    }

    private void reverseSayHiAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reverse_button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnSayHi.setVisibility(View.GONE);
                reversePortfolioAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnSayHi.startAnimation(animation);
    }

    private void clearAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnClear.startAnimation(animation);
    }

    private void reverseClearAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reverse_button_animation);
        // Set an AnimationListener to detect the end of the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, add any additional logic here
                binding.btnClear.setVisibility(View.GONE);
                reverseSayHiAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if set to repeat)
            }
        });
        binding.btnClear.startAnimation(animation);
    }

    private void handleCommand() {
        String command = binding.inputEditText.getText().toString();
        binding.consoleTextView.append("\n" + command);
        if (command.contains(getString(R.string.ls_menu))){
            lsMenu();
        }else if (command.contains(getString(R.string.clear))){
            clear();
        }else{
            String text = command.replace(getString(R.string.terminal_start), "");
            if (!TextUtils.isEmpty(text))
                commandNotFound(text);
        }
        binding.inputEditText.getText().clear();
        binding.inputEditText.setText(getString(R.string.terminal_start));
        // placing cursor at the end of the text
        binding.inputEditText.setSelection(binding.inputEditText.getText().length());
        binding.scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void commandNotFound(String command){
        // Create a SpannableString with the desired color for the text
        String errorMessage = getString(R.string.zsh);
        SpannableString spannableString = new SpannableString("\n" + errorMessage);

        // Set the color for the error message
        int color = Color.RED; // Change this to the desired color
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        spannableString.setSpan(colorSpan, 0, errorMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Append the SpannableString to the TextView
        binding.consoleTextView.append(spannableString);
        binding.consoleTextView.append(String.format("%s%s", getString(R.string.command_not_found), command));
    }

    private void clear(){
        String[] consoleText = binding.consoleTextView.getText().toString().split("\n");
        binding.consoleTextView.setText(consoleText[0]);

        reverseCircularRevealImage(binding.imageView);
    }

    private void lsMenu(){
        binding.consoleTextView.append("\n");
        // Replace the placeholder with the colored text
        String finalText = String.format("%s%s\n%s\n%s\n%s\n%s", binding.consoleTextView.getText().toString(), getString(R.string.about), getString(R.string.portfolio), getString(R.string.experience), getString(R.string.say_hi), getString(R.string.clear));
        SpannableString spannableString = new SpannableString(finalText);

        // Find the index of the colored text in the final text
        int startIndex = finalText.indexOf(String.format("%s\n%s\n%s\n%s\n%s", getString(R.string.about), getString(R.string.portfolio), getString(R.string.experience), getString(R.string.say_hi), getString(R.string.clear)));
        int endIndex = startIndex + String.format("%s\n%s\n%s\n%s\n%s", getString(R.string.about), getString(R.string.portfolio), getString(R.string.experience), getString(R.string.say_hi), getString(R.string.clear)).length();

        // Set the color span for the colored text
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.folder));
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set a bold span for the entire text
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.consoleTextView.setText(spannableString);

        binding.btnAbout.setVisibility(View.VISIBLE);
        aboutAnimation();
    }

    private void setUpPagerAdapter(String[] images) {
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(MainActivity.this, images);
        viewPager.setAdapter(screenSlidePagerAdapter);
    }

    public void setVisibilityGone(){
        viewPager.setVisibility(View.GONE);
        scrolledPagePosition = -1;
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (scrolledPagePosition != -1){
                if (scrolledPagePosition != position){
                    if (position > scrolledPagePosition){
                        CardView cardView = screenSlidePagerAdapter.cardViews[position - 1];
                        cardView.setVisibility(View.INVISIBLE);
                    } else {
                        CardView cardView = screenSlidePagerAdapter.cardViews[position + 1];
                        cardView.setVisibility(View.INVISIBLE);
                    }
                    scrolledPagePosition = position;
                }
            } else {
                scrolledPagePosition = position;
                CardView cardView = screenSlidePagerAdapter.cardViews[position];
                cardView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageSelected(int position) {
            CardView cardView = screenSlidePagerAdapter.cardViews[position];
            cardView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            if (viewPager.getVisibility() == View.VISIBLE){
                setVisibilityGone();
            }else{
                super.onBackPressed();
            }
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private void getNotableProjects(){
        Projects project2 = new Projects(
                "Skye",
                "https://lh3.googleusercontent.com/13Hu_EAaR5REMBBikzVC9SjHwuktd0Dx51YCDIvO-0r5vYp-TF7hR_GE4WWAW_rgs-c=s180-rw",
                new String[]{
                        "https://lh3.googleusercontent.com/GmylmXAHMGkCbMZK5jqRoci51YugTGQxjjWaRmmQCqrtjWkDSav03Sq9PmH5Hc-M1EI=w720-h310-rw",
                        "https://lh3.googleusercontent.com/TO_SNb_w2A4nHM0-fWEwjfl_jckebfgCadL8Go1GZ-1winyHtYoBXvsx0F6hTH_x1eL5=w720-h310-rw",
                        "https://lh3.googleusercontent.com/NITDt9iGdYXmCxrvU7Qqzii5MrskYdoZGIshvL1upWB10130OxPjIZihkizdq-BvuEPT=w720-h310-rw",
                        "https://lh3.googleusercontent.com/pzJWuDzE2uOfOMHeGlNKnf9a3zj5HcGnfsFwg7mbbX-t5Y9uKCfiKRIBAuav9LSqhVbI=w720-h310-rw",
                        "https://lh3.googleusercontent.com/OzN2FemEenT7sIXVrCiSU1W-Yyt8GgPAQIwWlKquAF6s_IAY_4g-gLEHaF9bvEERLAoY=w720-h310-rw"},
                "https://play.google.com/store/apps/details?id=com.geeknat.skye"
        );
        projects.add(project2);

        Projects project1 = new Projects(
                "ZingKE",
                "https://lh3.googleusercontent.com/_ixWWKKyxWTT0ayYcKuS6xps4h2dG85vvj3bEEj33Fzs_9egU7bgzDvnUIizBEGTFpc=s180-rw",
                new String[]{
                        "https://lh3.googleusercontent.com/zBeAjRn1VLKXVF89LN2_P6moK6f431btI-cFdnZd4M2i0LzXt6rmzPVEGOlarofiIw=w720-h310-rw",
                        "https://lh3.googleusercontent.com/Bf-hTcWAjtz0rRm3o9b_FTxVkp3KmGEGntCswNd2YDtDMsa9rSIR7xm3HPVkcO_3poL2=w720-h310-rw",
                        "https://lh3.googleusercontent.com/nKgybPnQCvAhFWHop37bIUJiUjP-zWv9pbyDjQAYK3mSE9flR5Bp_jSyl28xZ-W8pFkh=w720-h310-rw",
                        "https://lh3.googleusercontent.com/pPnwqe7nZDWOqpXu2sfPV_4G9srjLu2JJFZehL3eUziIpzYmNM2BBaKUBRtxhEu_XBvw=w720-h310-rw",
                        "https://lh3.googleusercontent.com/pv3LE3Fbt20-b0qNOvtFZD-FlLkcsPFzNzeaSb67vH6DCXbbXTTSDJDT8zcvSWLSwQ=w720-h310-rw",
                        "https://lh3.googleusercontent.com/nvcrQ2G7iG8endNB76mMs6lx1Cd8ybyV6MGXX0FZnp-NQXg_28RPQ9OWxomx5T7haVQ=w720-h310-rw",
                        "https://lh3.googleusercontent.com/26CNZ7OePhsJnhRp3r0fTaO6QZrFPKM0FdxydBj49Hd9x91GYIri2GtKfsUhSqWXDA=w720-h310-rw",
                        "https://lh3.googleusercontent.com/3lcHoEOD9XYlVxergeoBxun2jib2ucz6K-1i_xNWdSzMEIQS3X95r5XqOoueQqK5vAQ=w720-h310-rw"},
                "https://play.google.com/store/apps/details?id=zing.app.co.ke.zing"
        );
        projects.add(project1);

    }

    private void fetchUserGitHub() {
        GitHubApiManager gitHubApiManager = GitHubApiManager.getInstance();
        gitHubApiManager.getGitHubUser(new Callback<GitHub>() {
            @Override
            public void onResponse(@NotNull Call<GitHub> call, @NotNull Response<GitHub> response) {
                if (response.isSuccessful()) {
                    GitHub gitHub = response.body();
                    if (gitHub != null) {
                        binding.tvPublicRepos.setText(String.valueOf(gitHub.getPublic_repos()));
                        binding.tvFollowers.setText(String.valueOf(gitHub.getFollowers()));
                        binding.tvFollowing.setText(String.valueOf(gitHub.getFollowing()));
                        binding.tvLocation.setText(gitHub.getLocation());
                    }
                } else {
                    String message = response.message();
                    try {
                        if (response.errorBody() != null) {
                            message = response.errorBody().string();
                            JSONObject error = new JSONObject(message);
                            Toast.makeText(MainActivity.this, error.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException | JSONException ignored) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<GitHub> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void openURLExternal(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void openPlayStore(){
        openURLExternal(projects.get(clickedPosition).getProjectUrl());
    }

    private void placeCall(String phone){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                Bundle args = new Bundle();
                args.putString(CustomDialog.CUSTOM_DIALOG_TITLE, getResources().getString(R.string.permission_needed));
                args.putString(CustomDialog.CUSTOM_DIALOG_MESSAGE, getResources().getString(R.string.phone_call_permission));

                DialogFragment dialogFragment = new CustomDialog();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.request_phone_call_permission));

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,  new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else{
            Uri number = Uri.parse("tel:" + Uri.encode(phone));
            Intent dial = new Intent(Intent.ACTION_CALL, number);
            startActivity(dial);
        }
    }

    private void sendEmail(){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.hello_there));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.hello));
        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_email_using)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_email_clients_installed), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.profile));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.share_app), BuildConfig.APPLICATION_ID));
        startActivityForResult(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)), 202);
    }

    private void rateApp(){
        Context context = MainActivity.this;
        Uri uri = Uri.parse(String.format("%s%s", getResources().getString(R.string.market), context.getPackageName()));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count on Play market backstack, after pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,  Uri.parse(String.format("%s%s", getResources().getString(R.string.playstore_details), context.getPackageName()))));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnAbout || id == R.id.btnExperience || id == R.id.btnPortfolio || id == R.id.btnSayHi || id == R.id.btnClear){
            // Get the center for the clipping circle
            int cx = view.getWidth() / 2;
            int cy = view.getHeight() / 2;

            // Get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // Create the circular reveal animation
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

            // Set a listener to handle the animation end
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Hide the button after the animation ends
                    // view.setVisibility(View.GONE);
                }
            });

            // Start the animation
            anim.start();

            if(id == R.id.btnAbout){
                transformImage();
                circularRevealImage(binding.imageView);
            }else if(id == R.id.btnClear){
                binding.inputEditText.setText(String.format("%s%s", getString(R.string.terminal_start), getString(R.string.clear)));
                binding.inputEditText.setSelection(binding.inputEditText.getText().length());
                binding.inputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        }


        switch (view.getId()){
            case R.id.llLinkedIn:
                openURLExternal(Configs.LINKEDIN_URL);
                break;
            case R.id.llGitHub:
                openURLExternal(Configs.GITHUB_URL);
                break;
            case R.id.llWebsite:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.website_under_development), Toast.LENGTH_SHORT).show();
                openURLExternal(Configs.WEBSITE_URL);
                break;
            case R.id.llBlog:
//                openURLExternal(Configs.BLOG_URL);
                Toast.makeText(MainActivity.this, getResources().getString(R.string.blog_under_development), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void circularRevealImage(View view) {
        int centerX = view.getWidth() / 2;
        int centerY = view.getHeight() / 2;
        float startRadius = 0;
        float endRadius = (float) Math.hypot(centerX, centerY);

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                view,
                centerX,
                centerY,
                startRadius,
                endRadius
        );

        circularReveal.setDuration(1000);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());

        view.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private void reverseCircularRevealImage(View view) {
        int centerX = view.getWidth() / 2;
        int centerY = view.getHeight() / 2;
        float startRadius = (float) Math.hypot(centerX, centerY);
        float endRadius = 0;

        Animator circularHide = ViewAnimationUtils.createCircularReveal(
                view,
                centerX,
                centerY,
                startRadius,
                endRadius
        );

        circularHide.setDuration(1000);
        circularHide.setInterpolator(new AccelerateDecelerateInterpolator());

        circularHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                view.setVisibility(View.INVISIBLE);
                reverseClearAnimation();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {
            }
        });

        circularHide.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.certified_android_software_engineer), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuEmail:
                sendEmail();
                return true;
            case R.id.menuCall:
                placeCall(getResources().getString(R.string.phone));
                return true;
            case R.id.menuShare:
                shareApp();
                return true;
            case R.id.menuRate:
                rateApp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    placeCall(getResources().getString(R.string.phone));
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
