package com.example.neighbormaterebuild.ui.search.complete;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Picker;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.model.Util;
import com.example.neighbormaterebuild.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;


public class SearchFormFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner spinnerSex, spinnerHeight, spinnerEra, spinnerFigure, spinnerProfession, spinnerAnnualIcome,  spinnerSortOrder;
    private Button btnSubmitSearchForm;
    private User user;
    private Picker picker;
    List<Picker> pickerList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (Metadata.getInstance().getData() == null) {
            try {
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            } catch (Exception ignored) {
            }
        }
        super.onViewCreated(view, savedInstanceState);
        spinnerSex = view.findViewById(R.id.spinnerSex);
        spinnerHeight = view.findViewById(R.id.spinnerHeight);
        spinnerEra = view.findViewById(R.id.spinnerEra);
        spinnerFigure = view.findViewById(R.id.spinnerFigure);
        spinnerProfession = view.findViewById(R.id.spinnerProfession);
        spinnerAnnualIcome = view.findViewById(R.id.spinnerAnnualIcome);
        spinnerSortOrder = view.findViewById(R.id.spinnerSortOrder);
        btnSubmitSearchForm = view.findViewById(R.id.btnSubmitSearchForm);

        user = new User();
        initData();
    }

    private void initData() {
        Metadata body = Metadata.getInstance();
        if (body.getData() == null) return;

        List<Util> sexList = new ArrayList<>();
        sexList.add(new Util("", "指定なし", "指定なし"));
        sexList.addAll(body.getData().getUserProfileList().getSex());
        ArrayAdapter<Util> adapter1 = new ArrayAdapter<Util>(getContext(), R.layout.spinner_item, sexList);
        spinnerSex.setAdapter(adapter1);

        List<Util> heightList = new ArrayList<>();
        heightList.add(new Util("", "指定なし", "指定なし"));
        heightList.addAll(body.getData().getUserProfileList().getHeight());
        ArrayAdapter<Util> adapter2 = new ArrayAdapter<Util>(getContext(), R.layout.spinner_item, heightList);
        spinnerHeight.setAdapter(adapter2);

        List<Util> incomeList = new ArrayList<>();
        incomeList.add(new Util("", "指定なし", "指定なし"));
        incomeList.addAll(body.getData().getUserProfileList().getIncome());
        ArrayAdapter<Util> adapter3 = new ArrayAdapter<Util>(getContext(), R.layout.spinner_item, incomeList);
        spinnerAnnualIcome.setAdapter(adapter3);

        List<Util> professionalList = new ArrayList<>();
        professionalList.add(new Util("", "指定なし", "指定なし"));
        professionalList.addAll(body.getData().getUserProfileList().getJob());
        ArrayAdapter<Util> adapter4 = new ArrayAdapter<Util>(getContext(), R.layout.spinner_item, professionalList);
        spinnerProfession.setAdapter(adapter4);

        List<Util> eraList = new ArrayList<>();
        eraList.add(new Util("", "指定なし", "指定なし"));
        eraList.addAll(body.getData().getUserProfileList().getAge());
        ArrayAdapter<Util> adapter5 = new ArrayAdapter<Util>(getContext(), R.layout.spinner_item, eraList);
        spinnerEra.setAdapter(adapter5);

        List<Util> figureList = new ArrayList<>();
        figureList.add(new Util("", "指定なし", "指定なし"));
        figureList.addAll(body.getData().getUserProfileList().getStyle());
        ArrayAdapter<Util> adapter6 = new ArrayAdapter<Util>(getContext(), R.layout.spinner_item, figureList);
        spinnerFigure.setAdapter(adapter6);


        picker = new Picker();
        pickerList = new ArrayList<>();
        pickerList.add(new Picker("指定なし", 0));
        pickerList.add(new Picker("登録順", 1));
        pickerList.add(new Picker("アクセス順", 2));
        ArrayAdapter<Picker> adapter7 = new ArrayAdapter<Picker>(getContext(), R.layout.spinner_item, pickerList);
        spinnerSortOrder.setAdapter(adapter7);

        spinnerEra.setOnItemSelectedListener(this);
        spinnerSex.setOnItemSelectedListener(this);
        spinnerHeight.setOnItemSelectedListener(this);
        spinnerAnnualIcome.setOnItemSelectedListener(this);
        spinnerProfession.setOnItemSelectedListener(this);
        spinnerFigure.setOnItemSelectedListener(this);
        spinnerSortOrder.setOnItemSelectedListener(this);

        btnSubmitSearchForm.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0 && view != null)    {
            ((AppCompatTextView) view).setTextColor(getContext().getResources().getColor(R.color.textDefault));
            //Log.d("SearchForm", ((AppCompatTextView) view).getText() + "selected and change color");
        }

        switch (parent.getId()) {
            case R.id.spinnerSex:
                Util sexModel = (Util) parent.getItemAtPosition(position);
                user.setSex(sexModel.getValue());
                break;
            case R.id.spinnerEra:
                Util ageModel = (Util) parent.getItemAtPosition(position);
                user.setAge(ageModel.getValue());
                break;
            case R.id.spinnerHeight:
                Util heightModel = (Util) parent.getItemAtPosition(position);
                user.setHeight(heightModel.getValue());
                break;
            case R.id.spinnerFigure:
                Util styleModel = (Util) parent.getItemAtPosition(position);
                user.setStyle(styleModel.getValue());
                break;
            case R.id.spinnerAnnualIcome:
                Util icomeModel = (Util) parent.getItemAtPosition(position);
                user.setIncome(icomeModel.getValue());
                break;
            case R.id.spinnerProfession:
                Util jobModel = (Util) parent.getItemAtPosition(position);
                user.setJob(jobModel.getValue());
                break;
            case R.id.spinnerSortOrder:
                Picker picker1 = (Picker) parent.getItemAtPosition(position);
                user.setPickerList(picker1.getValue() + "");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SearchCompleteFragment fragment = new SearchCompleteFragment();
        bundle.putString("sex", user.getSex());
        bundle.putString("age", user.getAge());
        bundle.putString("job", user.getJob());
        bundle.putString("height", user.getHeight());
        bundle.putString("style", user.getStyle());
        bundle.putString("income", user.getIncome());
        bundle.putString("sort_by", user.getPickerList());
        fragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fragmentSearchForm, fragment);
        fragmentTransaction.addToBackStack("fragment");
        fragmentTransaction.commit();
    }
}