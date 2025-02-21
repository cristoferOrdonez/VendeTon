package com.example.estaut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.estaut.adapters.TaskPomodoroListViewAdapter;
import com.example.estaut.db.DbPomodoro;
import com.example.estaut.entities.TaskCard;
import com.example.estaut.entities.TimerPomodoro;

import java.util.ArrayList;
import java.util.Locale;

public class Pomodoro extends AppCompatActivity {

    //atributos
    public static final int ID_INFORMATION_TIMER = 1;
    private static final long PLUS_ONE_TIMER = 60000;//1000;
    public static final long POINT_OF_START_TIMER_DEFAULT = 1500000;//10000;
    public static final long TIME_BREAK_TIMER_DEFAULT = 300000;//5000;
    public static final double VOLUME_ALARMS_DEFAULT = 1;
    private static final int MAX_VALUE_FOR_TASK_CARDS = 100;
    private static final boolean COMPLETE_TASK = true, INCOMPLETE_TASK = false;
    private long pointOfStartTimer;
    private long TimeBreakTimer;
    private double volumeAlarms;
    private long timerTime, numPomodoros;
    private ImageButton buttonMoreOptionsTasks, buttonAcceptNewTask, buttonCancelNewTask, buttonBackTasks, buttonBack, buttonSettings, buttonTasks, buttonPlayPauseTimer, buttonReplayTimer, buttonMoreTime, buttonRestore;
    private CountDownTimer timer;
    private boolean timerRun, pomodoroOrBreak, incompleteTasksShowed;
    private TextView textViewWordCounterNewTask, textViewTimer, textViewPomodoros, textViewPlus1min, textViewTasksIndication;
    private ProgressBar progressBarTimer;
    private MediaPlayer soundPomodoro, soundPomodoroBreak;
    private ImageView circleTimer;
    private TimerPomodoro settingsPomodoro;
    private DrawerLayout drawerLayout;
    private LinearLayout linearLayoutTasks;
    private Button buttonAddTask;
    private CardView cardViewNewTask;
    private TextView multilineTextNewTask;
    public static ArrayList<TaskCard> tasks;
    private ListView listViewTasks;
    private TaskPomodoroListViewAdapter adapterTasks;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        incompleteTasksShowed = true;

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> adjustListViewSize());

        listViewTasks = findViewById(R.id.listViewTasks);

        setListViewTasks();

        textViewWordCounterNewTask = findViewById(R.id.textViewWordCounterCardNewTask);
        textViewWordCounterNewTask.setText("0/" + MAX_VALUE_FOR_TASK_CARDS);

        cardViewNewTask = findViewById(R.id.cardViewNewTask);


        multilineTextNewTask = findViewById(R.id.editTextNewTask);
        multilineTextNewTask.setFilters(new InputFilter[]{new InputFilter.LengthFilter((MAX_VALUE_FOR_TASK_CARDS))});

        multilineTextNewTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int words = charSequence.length();
                textViewWordCounterNewTask.setText(words + "/" + MAX_VALUE_FOR_TASK_CARDS);

                if(words == MAX_VALUE_FOR_TASK_CARDS)
                    textViewWordCounterNewTask.setTextColor(getResources().getColor(R.color.red_max_words_task_card, null));
                else
                    textViewWordCounterNewTask.setTextColor(getResources().getColor(R.color.white, null));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonAcceptNewTask = findViewById(R.id.imageButtonAcceptNewTask);
        buttonCancelNewTask = findViewById(R.id.imageButtonCancelNewTask);

        buttonAddTask = findViewById(R.id.buttonAddTask);
        buttonAddTask.setOnClickListener(i -> {

            deleteLayoutAndVisibility(buttonAddTask, true);
            setLayoutParamsCardViewNewTask(true);
            multilineTextNewTask.setEnabled(true);
            adjustListViewSize();

        });

        buttonAcceptNewTask.setOnClickListener(i -> {

            tasks.add(new TaskCard(multilineTextNewTask.getText().toString(), false));
            setListViewTasks();
            deleteLayoutAndVisibility(cardViewNewTask, true);
            setLayoutParamsButtonAddTask(true);
            multilineTextNewTask.setText("");
            multilineTextNewTask.setEnabled(false);
            adjustListViewSize();

        });

        buttonCancelNewTask.setOnClickListener(i -> {

            deleteLayoutAndVisibility(cardViewNewTask, true);
            setLayoutParamsButtonAddTask(true);
            multilineTextNewTask.setText("");
            multilineTextNewTask.setEnabled(false);
            adjustListViewSize();

        });

        textViewTasksIndication = findViewById(R.id.textViewTasksIndication);

        drawerLayout = findViewById(R.id.drawerLayout);
        linearLayoutTasks = findViewById(R.id.linearLayoutTasks);

        setTimerValues();

        buttonTasks = findViewById(R.id.imageButtonTasks);
        buttonTasks.setOnClickListener(i -> {

            drawerLayout.openDrawer(linearLayoutTasks);
            showFadeOutAnimation(buttonTasks, 250);

        });

        buttonBackTasks = findViewById(R.id.imageButtonBackTasks);
        buttonBackTasks.setOnClickListener(i -> {

            drawerLayout.closeDrawer(linearLayoutTasks);
            showFadeInAnimation(buttonAddTask, 250);

        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                if(buttonTasks.getVisibility() != View.INVISIBLE)
                    showFadeOutAnimation(buttonTasks, 250);

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

                if(buttonTasks.getVisibility() != View.VISIBLE)
                    showFadeInAnimation(buttonTasks, 250);

                if(cardViewNewTask.getVisibility() == View.VISIBLE){

                    deleteLayoutAndVisibility(cardViewNewTask, false);
                    setLayoutParamsButtonAddTask(false);
                    multilineTextNewTask.setText("");
                    multilineTextNewTask.setEnabled(false);

                }

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        buttonSettings = findViewById(R.id.imageButtonSettingsPomodoro);
        buttonSettings.setOnClickListener(i -> moveToSettings());

        buttonRestore = findViewById(R.id.imageButtonRestorePomodoros);
        buttonRestore.setOnClickListener(i -> restorePomodoros());

        soundPomodoro = MediaPlayer.create(this, R.raw.ping_pomodoro);
        soundPomodoroBreak = MediaPlayer.create(this, R.raw.ping_pomodoro_break);
        soundPomodoro.setVolume((float)volumeAlarms, (float)volumeAlarms);
        soundPomodoroBreak.setVolume((float)volumeAlarms, (float)volumeAlarms);

        numPomodoros = 0;
        textViewPomodoros = findViewById(R.id.textViewPomodoros);
        textViewPlus1min = findViewById(R.id.textViewPlus1min);
        buttonMoreTime = findViewById(R.id.imageButtonMoreTime);
        buttonMoreTime.setOnClickListener(i -> plusOne());
        disableMoreTimeButton();

        buttonBack = findViewById(R.id.imageButtonBackPomodoro);
        buttonBack.setOnClickListener(i -> backToHome());

        circleTimer = findViewById(R.id.imageViewCircleTimer);

        timerRun = false;
        textViewTimer = findViewById(R.id.textViewTimer);
        updateCountDownText();

        buttonReplayTimer = findViewById(R.id.imageButtonReplayTimer);
        buttonPlayPauseTimer = findViewById(R.id.imageButtonPlayPauseTimer);

        disableReplayButton();
        disableRestoreButton();

        pomodoroOrBreak = true;

        buttonPlayPauseTimer.setOnClickListener(i -> {
                if(timerRun){
                    pausePomodoro();
                } else {
                    if(pomodoroOrBreak)
                        startPomodoro();
                    else
                        startPomodoroBreak();
                }
        });

        buttonReplayTimer.setOnClickListener(i -> replayPomodoro());

        progressBarTimer = findViewById(R.id.progressBarTimer);
        progressBarTimer.setMax((int) pointOfStartTimer);
        progressBarTimer.setProgress((int) pointOfStartTimer);

        buttonMoreOptionsTasks = findViewById(R.id.imageButtonMoreOptionsTasks);
        buttonMoreOptionsTasks.setOnClickListener(i -> showPopupMenuMoreOptionsTasks());

        setLayoutParams();

    }

    private void showPopupMenuMoreOptionsTasks(){

        popupMenu = new PopupMenu(this, buttonMoreOptionsTasks);
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.buttonHideIncompleteTasks) {
                    if(incompleteTasksShowed){

                        menuItem.setTitle("show incomplete tasks");


                    }
                    return true;
                } else if (menuItem.getItemId() == R.id.buttonDeleteIncompleteTasks){
                    clearIncompleteOrCompleteTasks(INCOMPLETE_TASK);
                    setListViewTasks();
                    return true;
                } else if (menuItem.getItemId() == R.id.buttonDeleteCompleteTasks){
                    clearIncompleteOrCompleteTasks(COMPLETE_TASK);
                    setListViewTasks();
                    return true;
                } else if (menuItem.getItemId() == R.id.buttonDeleteAllTask) {
                    tasks.clear();
                    setListViewTasks();
                    return true;
                } else
                    return false;
            }
        });
        popupMenu.inflate(R.menu.popup_menu_more_options_tasks);
        popupMenu.show();

    }

    private void clearIncompleteOrCompleteTasks(boolean completeOrIncomplete){

        ArrayList<TaskCard> tasks = new ArrayList<>();

        if(completeOrIncomplete) {
            for (TaskCard taskCardT : this.tasks)
                if (!taskCardT.isCompleted())
                    tasks.add(taskCardT);
        } else {
            for (TaskCard taskCardF : this.tasks)
                if (taskCardF.isCompleted())
                    tasks.add(taskCardF);
        }

        this.tasks = tasks;

    }

    private void editTask(View v, int idTask){

        CardView cardView = v.findViewById(R.id.cardViewTask);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.gray_task_card_being_edited, null));
        deleteLayoutAndVisibility(buttonAddTask, true);
        setLayoutParamsCardViewNewTask(true);
        multilineTextNewTask.setEnabled(true);
        adjustListViewSize();

        TaskCard taskcard = tasks.get(idTask);
        multilineTextNewTask.setText(taskcard.getTask());
        buttonCancelNewTask.setOnClickListener(i -> {

            deleteLayoutAndVisibility(cardViewNewTask, true);
            setLayoutParamsButtonAddTask(true);
            multilineTextNewTask.setText("");
            multilineTextNewTask.setEnabled(false);
            adjustListViewSize();
            cardView.setCardBackgroundColor(getResources().getColor(R.color.gray_task_card, null));

            buttonCancelNewTask.setOnClickListener(o -> {

                deleteLayoutAndVisibility(cardViewNewTask, true);
                setLayoutParamsButtonAddTask(true);
                multilineTextNewTask.setText("");
                multilineTextNewTask.setEnabled(false);
                adjustListViewSize();

            });

        });

        buttonAcceptNewTask.setOnClickListener(i -> {

            tasks.get(idTask).setTask(multilineTextNewTask.getText().toString());
            setListViewTasks();
            deleteLayoutAndVisibility(cardViewNewTask, true);
            setLayoutParamsButtonAddTask(true);
            multilineTextNewTask.setText("");
            multilineTextNewTask.setEnabled(false);
            adjustListViewSize();

            buttonAcceptNewTask.setOnClickListener(o -> {

                tasks.add(new TaskCard(multilineTextNewTask.getText().toString(), false));
                setListViewTasks();
                deleteLayoutAndVisibility(cardViewNewTask, true);
                setLayoutParamsButtonAddTask(true);
                multilineTextNewTask.setText("");
                multilineTextNewTask.setEnabled(false);
                adjustListViewSize();

            });

        });
    }

    private void showPopupMenuTaskDeleteOrEdit(int idItem, View v){

        popupMenu = new PopupMenu(this, v);
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.buttonEditTask) {
                    editTask(v, idItem);
                    return true;
                } else if (menuItem.getItemId() == R.id.buttonDeleteTask){
                    tasks.remove(idItem);
                    setListViewTasks();
                    return true;
                } else
                    return false;
            }
        });
        popupMenu.inflate(R.menu.popup_menu_task_pomodoro_delete_or_edit);
        popupMenu.show();

    }

    private void adjustListViewSize(){

        LinearLayout.LayoutParams ln = (LinearLayout.LayoutParams) listViewTasks.getLayoutParams();
        ln.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        ln.weight = 0;
        listViewTasks.setLayoutParams(ln);


        linearLayoutTasks.post(new Runnable() {
            @Override
            public void run() {

                int linearLayoutHeight = linearLayoutTasks.getHeight();

                int contenidoHeight = 0;
                for (int i = 0; i < linearLayoutTasks.getChildCount(); i++) {
                    View child = linearLayoutTasks.getChildAt(i);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();

                    contenidoHeight += child.getHeight() + params.topMargin + params.bottomMargin;
                }

                LinearLayout.LayoutParams newLayoutListview = (LinearLayout.LayoutParams) listViewTasks.getLayoutParams();

                if (contenidoHeight >= linearLayoutHeight) {

                    newLayoutListview.height = 0;
                    newLayoutListview.weight = 1;

                } else {

                    newLayoutListview.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    newLayoutListview.weight = 0;

                }

                listViewTasks.setLayoutParams(newLayoutListview);

            }

        });

    }

    private void setListViewTasks(){

        if(listViewTasks == null)
            listViewTasks = findViewById(R.id.listViewTasks);

        if(tasks == null)
            tasks = new ArrayList<>();

        adapterTasks = new TaskPomodoroListViewAdapter(this, tasks);
        listViewTasks.setAdapter(adapterTasks);

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(popupMenu != null)
                    popupMenu.dismiss();

                showPopupMenuTaskDeleteOrEdit(i, view);
            }
        });

    }

    private void setTimerValues(){

        DbPomodoro dbPomodoro = new DbPomodoro(this);

        settingsPomodoro = dbPomodoro.showSettingsTimerPomodoro(ID_INFORMATION_TIMER);

        if(settingsPomodoro == null) {

            dbPomodoro.setTimerDefault(POINT_OF_START_TIMER_DEFAULT, TIME_BREAK_TIMER_DEFAULT, VOLUME_ALARMS_DEFAULT); //cambiar volumen
            settingsPomodoro = dbPomodoro.showSettingsTimerPomodoro(ID_INFORMATION_TIMER);

        }

        pointOfStartTimer = settingsPomodoro.getTimePomodoro();
        TimeBreakTimer = settingsPomodoro.getTimeBreak();
        volumeAlarms = settingsPomodoro.getVolume();

        timerTime = pointOfStartTimer;

    }

    private void backToHome(){

            startActivity(new Intent(this, MainActivity.class));
            finish();

    }

    private void moveToSettings(){

        startActivity(new Intent(this, SettingsPomodoro.class));
        finish();

    }

    private void startPomodoro(){

        pomodoroOrBreak = true;

        timer = new CountDownTimer(timerTime, 50) {
            @Override
            public void onTick(long l) {

                timerTime = l;

                if(timerTime == pointOfStartTimer && buttonMoreTime.isEnabled())
                    disableMoreTimeButton();
                if(timerTime != pointOfStartTimer && !buttonMoreTime.isEnabled())
                    enableMoreTimeButton();

                progressBarTimer.setProgress((int)timerTime, true);
                updateCountDownText();

            }

            @Override
            public void onFinish() {

                soundPomodoro.start();
                progressBarTimer.setMax((int) TimeBreakTimer);
                progressBarTimer.setProgress(0);
                progressBarTimer.setProgress((int) TimeBreakTimer);
                timerTime = TimeBreakTimer;
                textViewPomodoros.setText("Pomodoros: " + ++numPomodoros);
                startPomodoroBreak();

            }
        }.start();

        timerRun = true;

        buttonPlayPauseTimer.setImageResource(R.drawable.button_pause_timer_unite);
        disableReplayButton();
        disableRestoreButton();

    }

    private void pausePomodoro(){

        timer.cancel();
        timerRun = false;
        buttonPlayPauseTimer.setImageResource(R.drawable.button_play_timer_unite);
        enableReplayButton();

        if(numPomodoros != 0)
            enableRestoreButton();

    }

    private void replayPomodoro(){

        if(pomodoroOrBreak)
            timerTime = pointOfStartTimer;
        else
            timerTime = TimeBreakTimer;
        updateCountDownText();
        disableReplayButton();
        disableMoreTimeButton();
        progressBarTimer.setProgress((int)timerTime);

    }

    private void updateCountDownText(){

        int minutes = (int) (timerTime / 1000) / 60;
        int seconds = (int) (timerTime / 1000) % 60;

        String textTimerFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewTimer.setText(textTimerFormated);

    }

    private void startPomodoroBreak(){

        pomodoroOrBreak = false;

        timer = new CountDownTimer(timerTime, 50) {
            @Override
            public void onTick(long l) {

                timerTime = l;

                progressBarTimer.setProgress((int)timerTime, true);

                if(timerTime == TimeBreakTimer && buttonMoreTime.isEnabled())
                    disableMoreTimeButton();
                if(timerTime != TimeBreakTimer && !buttonMoreTime.isEnabled())
                    enableMoreTimeButton();

                updateCountDownText();

            }

            @Override
            public void onFinish() {

                soundPomodoroBreak.start();
                progressBarTimer.setMax((int) pointOfStartTimer);
                progressBarTimer.setProgress(0);
                progressBarTimer.setProgress((int) pointOfStartTimer);
                timerTime = pointOfStartTimer;
                startPomodoro();

            }
        }.start();

        timerRun = true;

        buttonPlayPauseTimer.setImageResource(R.drawable.button_pause_timer_unite);
        disableReplayButton();
        disableRestoreButton();

    }

    private void enableReplayButton(){

        buttonReplayTimer.setEnabled(true);
        buttonReplayTimer.setImageResource(R.drawable.replay_timer_button_enabled);

    }

    private void disableReplayButton(){

        buttonReplayTimer.setEnabled(false);
        buttonReplayTimer.setImageResource(R.drawable.replay_timer_button_disabled);

    }

    private void enableMoreTimeButton(){

        buttonMoreTime.setEnabled(true);
        buttonMoreTime.setImageResource(R.drawable.more_time_button_enabled);

    }

    private void disableMoreTimeButton(){

        buttonMoreTime.setEnabled(false);
        buttonMoreTime.setImageResource(R.drawable.more_time_button_disabled);

    }

    private void plusOne(){

        showAnimationPlusFive();

        if(timer != null) {

            timer.cancel();

            long timeToComparate;
            long newTime;

            if (pomodoroOrBreak)
                timeToComparate = pointOfStartTimer;
            else
                timeToComparate = TimeBreakTimer;

            newTime = timerTime + PLUS_ONE_TIMER;

            if (newTime > timeToComparate) {
                timer.onTick(timeToComparate);
                disableReplayButton();

                if(numPomodoros == 0)
                    disableRestoreButton();

            }else
                timer.onTick(newTime);

            if(timerRun) {
                if (pomodoroOrBreak)
                    startPomodoro();
                else
                    startPomodoroBreak();
            }
        }

    }

    private void showAnimationPlusFive() {
        // Hacer la ImageView visible
        textViewPlus1min.setVisibility(View.VISIBLE);

        // Animación de desvanecimiento (alpha)
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(500); // tiempo de animación en milisegundos

        // Animación de desvanecimiento (alpha)
        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setStartOffset(500); // tiempo de espera antes de empezar a desvanecer
        fadeOut.setDuration(500); // tiempo de animación en milisegundos

        // Combina las dos animaciones en un conjunto
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(fadeOut);

        // Asigna la animación al ImageView
        textViewPlus1min.startAnimation(animationSet);

        // Después de 1 segundo, oculta la ImageView
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textViewPlus1min.setVisibility(View.INVISIBLE);
            }
        }, 1000);
    }

    private void showFadeOutAnimation(View view, long duration){

        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setStartOffset(0);
        fadeOut.setDuration(duration);

        view.clearAnimation();
        view.startAnimation(fadeOut);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.INVISIBLE);
            }
        }, duration);

    }

    private void showFadeInAnimation(View view, long duration){

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(duration);

        view.clearAnimation();
        view.startAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        }, duration);

    }

    private void enableRestoreButton(){

        buttonRestore.setEnabled(true);
        buttonRestore.setImageResource(R.drawable.restore_button_enabled);

    }

    private void disableRestoreButton(){

        buttonRestore.setEnabled(false);
        buttonRestore.setImageResource(R.drawable.restore_button_disabled);

    }

    private void restorePomodoros(){

        numPomodoros = 0;
        textViewPomodoros.setText("Pomodoros: 0");
        pomodoroOrBreak = true;
        progressBarTimer.setMax((int) pointOfStartTimer);
        replayPomodoro();
        disableRestoreButton();

    }

    private void setLayoutParams(){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);

        //LayoutParams ButtonBack
        ConstraintLayout.LayoutParams newLayoutButtonBack = (ConstraintLayout.LayoutParams)buttonBack.getLayoutParams();
        newLayoutButtonBack.width = screenWidth / 10;
        newLayoutButtonBack.height = screenWidth / 10;
        int marginButtonBack = screenWidth / 30;
        newLayoutButtonBack.setMargins(marginButtonBack, (int)(marginButtonBack * 1.7), 0, 0);
        buttonBack.setLayoutParams(newLayoutButtonBack);

        //LayoutParams ButtonSettings
        ConstraintLayout.LayoutParams newLayoutButtonSettings = (ConstraintLayout.LayoutParams)buttonSettings.getLayoutParams();
        newLayoutButtonSettings.width = newLayoutButtonBack.width;
        newLayoutButtonSettings.height = newLayoutButtonBack.height;
        newLayoutButtonSettings.setMargins(0, (int)(marginButtonBack * 1.7), marginButtonBack, 0);
        buttonSettings.setLayoutParams(newLayoutButtonSettings);


        //LayoutParams ButtonReplayTimer
        ConstraintLayout.LayoutParams newLayoutButtonReplayTimer = (ConstraintLayout.LayoutParams)buttonReplayTimer.getLayoutParams();
        newLayoutButtonReplayTimer.width = (int)(screenWidth / 11.74);
        newLayoutButtonReplayTimer.height = (int)(screenWidth / 11.74);
        buttonReplayTimer.setLayoutParams(newLayoutButtonReplayTimer);

        //LayoutParams ButtonRestore
        ConstraintLayout.LayoutParams newLayoutButtonRestore = (ConstraintLayout.LayoutParams)buttonRestore.getLayoutParams();
        newLayoutButtonRestore.width = (int)(screenWidth / 11.74);
        newLayoutButtonRestore.height = (int)(screenWidth / 11.74);
        int marginButtonRestore = (int)(screenWidth / 29.35);
        newLayoutButtonRestore.setMargins(marginButtonRestore, 0, 0, 0);
        buttonRestore.setLayoutParams(newLayoutButtonRestore);

        //LayoutParams ButtonPlayPauseTimer
        ConstraintLayout.LayoutParams newLayoutButtonPlayPauseTimer = (ConstraintLayout.LayoutParams)buttonPlayPauseTimer.getLayoutParams();
        newLayoutButtonPlayPauseTimer.width = (int)(screenWidth / 6.85);
        newLayoutButtonPlayPauseTimer.height = (int)(screenWidth / 6.85);
        int marginButtonPlayPauseTimer = (int)(screenWidth / 6.85);
        newLayoutButtonPlayPauseTimer.setMargins(0, marginButtonPlayPauseTimer, 0, 0);
        buttonPlayPauseTimer.setLayoutParams(newLayoutButtonPlayPauseTimer);

        //LayoutParams ButtonMoreTime
        ConstraintLayout.LayoutParams newLayoutButtonMoreTime = (ConstraintLayout.LayoutParams)buttonMoreTime.getLayoutParams();
        newLayoutButtonMoreTime.width = newLayoutButtonReplayTimer.width;
        newLayoutButtonMoreTime.height = newLayoutButtonReplayTimer.height;
        buttonMoreTime.setLayoutParams(newLayoutButtonMoreTime);

        //LayoutParams progressBarTimer
        ConstraintLayout.LayoutParams newLayoutProgressBarTimer = (ConstraintLayout.LayoutParams)progressBarTimer.getLayoutParams();
        newLayoutProgressBarTimer.width = (int)(screenWidth / 1.28);
        newLayoutProgressBarTimer.height = (int)(screenWidth / 1.28);
        progressBarTimer.setLayoutParams(newLayoutProgressBarTimer);

        //LayoutParams progressBarTimer
        ConstraintLayout.LayoutParams newLayoutCircleTimer = (ConstraintLayout.LayoutParams)circleTimer.getLayoutParams();
        newLayoutCircleTimer.width = (int)(screenWidth / 1.0275);
        newLayoutCircleTimer.height = (int)(screenWidth / 1.0275);
        circleTimer.setLayoutParams(newLayoutCircleTimer);

        //LayoutParams textViewTimer
        ConstraintLayout.LayoutParams newLayoutTextViewTimer = (ConstraintLayout.LayoutParams)textViewTimer.getLayoutParams();
        newLayoutTextViewTimer.width = (int)(newLayoutProgressBarTimer.width * 0.9);
        newLayoutTextViewTimer.height = (int)(newLayoutProgressBarTimer.width * 0.3);
        textViewTimer.setLayoutParams(newLayoutTextViewTimer);

        //LayoutParams textViewPlus1min
        ConstraintLayout.LayoutParams newLayoutTextViewPlus1min = (ConstraintLayout.LayoutParams)textViewPlus1min.getLayoutParams();
        int marginTextViewPlus1min = (int)(screenWidth / 20.55);
        newLayoutTextViewPlus1min.width = (int)(newLayoutProgressBarTimer.width * 0.1875);
        newLayoutTextViewPlus1min.height = (int)(newLayoutProgressBarTimer.width * 0.0625);
        newLayoutTextViewPlus1min.setMargins(0, 0, 0, marginTextViewPlus1min);
        textViewPlus1min.setLayoutParams(newLayoutTextViewPlus1min);

        //LayoutParams textViewPomodoros
        ConstraintLayout.LayoutParams newLayoutTextViewPomodoros = (ConstraintLayout.LayoutParams)textViewPomodoros.getLayoutParams();
        newLayoutTextViewPomodoros.width = (int)(newLayoutProgressBarTimer.width * 0.5);
        newLayoutTextViewPomodoros.height = (int)(newLayoutProgressBarTimer.width * 0.1);
        textViewPomodoros.setLayoutParams(newLayoutTextViewPomodoros);

        //LayoutParams buttonTasks
        ConstraintLayout.LayoutParams newLayoutButtonTasks = (ConstraintLayout.LayoutParams) buttonTasks.getLayoutParams();
        newLayoutButtonTasks.width = newLayoutButtonPlayPauseTimer.width;
        newLayoutButtonTasks.height = newLayoutButtonPlayPauseTimer.height;
        int marginButtonTasks = newLayoutButtonTasks.width / 2;
        newLayoutButtonTasks.setMargins(0, 0, marginButtonTasks, marginButtonTasks);
        buttonTasks.setLayoutParams(newLayoutButtonTasks);

        //LayoutParams buttonBackTasks
        LinearLayout.LayoutParams newLayoutButtonBackTasks = (LinearLayout.LayoutParams) buttonBackTasks.getLayoutParams();
        newLayoutButtonBackTasks.width = newLayoutButtonBack.width;
        newLayoutButtonBackTasks.height = newLayoutButtonBack.height;
        int marginButtonBackTasks = marginButtonBack;
        newLayoutButtonBackTasks.setMargins(0, marginButtonBackTasks, marginButtonBackTasks, 0);
        buttonBackTasks.setLayoutParams(newLayoutButtonBackTasks);

        //LayoutParams buttonMoreOptionsTasks
        LinearLayout.LayoutParams newLayoutButtonMoreOptionTasks = (LinearLayout.LayoutParams) buttonMoreOptionsTasks.getLayoutParams();
        newLayoutButtonMoreOptionTasks.width = newLayoutButtonBackTasks.width;
        newLayoutButtonMoreOptionTasks.height = newLayoutButtonBackTasks.height;
        int marginButtonMoreOptionsTasks = marginButtonBackTasks;
        newLayoutButtonMoreOptionTasks.setMargins(0, marginButtonMoreOptionsTasks, marginButtonMoreOptionsTasks, 0);
        buttonMoreOptionsTasks.setLayoutParams(newLayoutButtonMoreOptionTasks);

        //LayoutParams textViewTasksIndication
        LinearLayout.LayoutParams newLayoutTextViewTasksIndication = (LinearLayout.LayoutParams) textViewTasksIndication.getLayoutParams();
        newLayoutTextViewTasksIndication.height = newLayoutButtonBackTasks.height + marginButtonBackTasks * 2;
        int paddingTextViewTasksIndication = marginButtonBackTasks;
        textViewTasksIndication.setLayoutParams(newLayoutTextViewTasksIndication);
        textViewTasksIndication.setPadding(paddingTextViewTasksIndication, paddingTextViewTasksIndication, paddingTextViewTasksIndication, paddingTextViewTasksIndication);

        //LayoutParams buttonAddTask
        setLayoutParamsButtonAddTask(false);

        //LayoutParams buttonAcceptCancelNewTask
        LinearLayout.LayoutParams newLayoutButtonNewTask = (LinearLayout.LayoutParams) buttonAcceptNewTask.getLayoutParams();
        newLayoutButtonNewTask.width = newLayoutButtonBackTasks.width;
        newLayoutButtonNewTask.height = newLayoutButtonBackTasks.height;
        int marginButtonAcceptCancelNewTask = marginButtonBackTasks;
        newLayoutButtonNewTask.setMargins(marginButtonAcceptCancelNewTask, marginButtonAcceptCancelNewTask, marginButtonAcceptCancelNewTask, marginButtonAcceptCancelNewTask);
        buttonAcceptNewTask.setLayoutParams(newLayoutButtonNewTask);
        buttonCancelNewTask.setLayoutParams(newLayoutButtonNewTask);

        //LayoutParams multilineTextNewTask
        LinearLayout.LayoutParams newLayoutMultilineNewTask = (LinearLayout.LayoutParams) multilineTextNewTask.getLayoutParams();
        newLayoutMultilineNewTask.height = newLayoutTextViewTasksIndication.height * 2;
        int marginMultilineNewTask = marginButtonBackTasks / 2;
        newLayoutMultilineNewTask.setMargins(marginMultilineNewTask, marginMultilineNewTask, marginMultilineNewTask, 0);
        multilineTextNewTask.setLayoutParams(newLayoutMultilineNewTask);

        //LayoutParamsCardView
        deleteLayoutAndVisibility(cardViewNewTask, false);

        //LayoutParams textViewWordCounterNewTask
        LinearLayout.LayoutParams newLayoutCounterWordNewTask = (LinearLayout.LayoutParams) textViewWordCounterNewTask.getLayoutParams();
        newLayoutCounterWordNewTask.height = newLayoutButtonNewTask.height + marginButtonAcceptCancelNewTask * 2;
        textViewWordCounterNewTask.setLayoutParams(newLayoutCounterWordNewTask);
        int paddingCounterWordNewTask = marginButtonAcceptCancelNewTask;
        textViewWordCounterNewTask.setPadding(0, paddingCounterWordNewTask * 2, paddingCounterWordNewTask, paddingCounterWordNewTask);

    }

    private void deleteLayoutAndVisibility(View view, boolean animation){

        if(animation)
            showFadeOutAnimation(view, 250);
        else
            view.setVisibility(View.INVISIBLE);

        LinearLayout.LayoutParams newLayoutView = (LinearLayout.LayoutParams) view.getLayoutParams();
        newLayoutView.width = 0;
        newLayoutView.height = 0;
        newLayoutView.setMargins(0, 0, 0, 0);
        view.setLayoutParams(newLayoutView);

    }

    private void setLayoutParamsButtonAddTask(boolean animation){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);

        //LayoutParams buttonAddTask
        LinearLayout.LayoutParams newLayoutButtonAddTask = (LinearLayout.LayoutParams) buttonAddTask.getLayoutParams();
        newLayoutButtonAddTask.height = (int)(screenWidth * 0.12);
        newLayoutButtonAddTask.width = ViewGroup.LayoutParams.MATCH_PARENT;
        int marginButtonAddTask = screenWidth / 30;
        int paddingButtonAddTask = newLayoutButtonAddTask.height / 4;
        newLayoutButtonAddTask.setMargins(marginButtonAddTask, marginButtonAddTask, marginButtonAddTask, marginButtonAddTask);
        buttonAddTask.setLayoutParams(newLayoutButtonAddTask);
        buttonAddTask.setPadding(paddingButtonAddTask, paddingButtonAddTask, paddingButtonAddTask, paddingButtonAddTask);

        if(animation){
            showFadeInAnimation(buttonAddTask, 250);
        } else
            buttonAddTask.setVisibility(View.VISIBLE);

    }

    private void setLayoutParamsCardViewNewTask(boolean animation){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);

        LinearLayout.LayoutParams newLayoutCardViewNewTask = (LinearLayout.LayoutParams) cardViewNewTask.getLayoutParams();
        newLayoutCardViewNewTask.width = ViewGroup.LayoutParams.MATCH_PARENT;
        newLayoutCardViewNewTask.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        int marginCardViewNewTask = screenWidth / 30;

        newLayoutCardViewNewTask.setMargins(marginCardViewNewTask, marginCardViewNewTask, marginCardViewNewTask, marginCardViewNewTask);

        cardViewNewTask.setLayoutParams(newLayoutCardViewNewTask);

        if(animation)
            showFadeInAnimation(cardViewNewTask, 250);
        else
            cardViewNewTask.setVisibility(View.VISIBLE);

    }

    public void onDestroy(){
        super.onDestroy();

        if(timer != null)
            timer.cancel();

    }

}