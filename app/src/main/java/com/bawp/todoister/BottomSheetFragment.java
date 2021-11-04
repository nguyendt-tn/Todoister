package com.bawp.todoister;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bawp.todoister.adapter.RecyclerViewAdapter;
import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.ShareViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText enterTask;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    private boolean isEdit;
    private  Priority priority;

     Calendar calendar = Calendar.getInstance();
     private ShareViewModel shareViewModel;

    public BottomSheetFragment()
    {

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.bottom_sheet,container,false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTask = view.findViewById(R.id.enter_todo_et);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup =view.findViewById(R.id.radioGroup_priority);
        saveButton = view.findViewById(R.id.save_todo_button);

        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrow = view.findViewById(R.id.tomorrow_chip);
        tomorrow.setOnClickListener(this);
        Chip nextWeek = view.findViewById(R.id.next_week_chip);
        nextWeek.setOnClickListener(this);



       return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        if(shareViewModel.getSelectedItem().getValue()!=null){

            isEdit =shareViewModel.getIsEdit();

            Task task = shareViewModel.getSelectedItem().getValue();
            Log.d("MY","onViewCreated: " + task.getTask());
        }

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shareViewModel = new ViewModelProvider(requireActivity())
                .get(ShareViewModel.class);


         calendarButton.setOnClickListener(v ->
         {
             calendarGroup.setVisibility(
                     calendarGroup.getVisibility() == View.GONE ? View.VISIBLE :View.GONE);
             Utils.hideSoftKeyboard(v);
         });


        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            calendar.clear();
            calendar.set(year,month,dayOfMonth);
            dueDate = calendar.getTime();

            //  Log.d("Cal","onViewCreated: ===> " + (month +1) + ",dayOfMonth" + dayOfMonth);

        });

        priorityButton.setOnClickListener(v2 -> {

            Utils.hideSoftKeyboard(v2);
            priorityRadioGroup.setVisibility(
                    priorityRadioGroup.getVisibility()== View.GONE ? View.VISIBLE :View.GONE);

            priorityRadioGroup.setOnCheckedChangeListener((radioGroup, checkId) -> {
                if(priorityRadioGroup.getVisibility() == View.VISIBLE)
                {
                    selectedButtonId = checkId;
                    selectedRadioButton = view.findViewById(selectedButtonId);

                    if(selectedRadioButton.getId() == R.id.radioButton_high)
                    {
                        priority = Priority.HIGH;
                    }else if (selectedRadioButton.getId() == R.id.radioButton_med)
                    {
                        priority = Priority.MEDIUM;
                    }else if(selectedRadioButton.getId()==R.id.radioButton_low){
                        priority = Priority.LOW;
                    }else {
                        priority = Priority.LOW;
                    }

                }else {
                    priority = Priority.LOW;
                }

            });
        });

        saveButton.setOnClickListener(view1 ->
                {
                    String task = enterTask.getText().toString().trim();

                    if(!TextUtils.isEmpty(task) && dueDate != null && priority != null) {
                        Task myTask = new Task(task,priority,
                                dueDate, Calendar.getInstance().getTime(), false);
                        if (isEdit) {
                            Task updateTask = shareViewModel.getSelectedItem().getValue();

                            updateTask.setTask(task);
                            updateTask.setDateCreated(Calendar.getInstance().getTime());
                            updateTask.setPriority(priority);
                            updateTask.setDueDate(dueDate);
                            TaskViewModel.update(updateTask);

                            shareViewModel.setIsEdit(false);

                        } else {
                            TaskViewModel.insert(myTask);
                        }
                        enterTask.setText("");
                        if(this.isVisible())
                        {
                            this.dismiss();
                        }


                    }else
                    {
                        Snackbar.make(saveButton, R.string.empty_field,Snackbar.LENGTH_LONG).show();
                    }
                });


    }

    @Override
    public void onClick(View v3) {
        int id = v3.getId();
        if(id == R.id.today_chip)
        {
            //set data for today
            calendar.add(Calendar.DAY_OF_YEAR,0);
            dueDate = calendar.getTime();
            Log.d("TIME","onClick: " + dueDate.toString());

        }else if(id == R.id.tomorrow_chip)
        {
            calendar.add(Calendar.DAY_OF_YEAR,1);
            dueDate = calendar.getTime();
            Log.d("TIME","onClick: " + dueDate.toString());
        }else if(id == R.id.next_week_chip)
        {
            calendar.add(Calendar.DAY_OF_YEAR,7);
            dueDate = calendar.getTime();
            Log.d("TIME","onClick: " + dueDate.toString());
        }


    }
}