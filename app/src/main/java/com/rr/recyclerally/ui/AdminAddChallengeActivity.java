package com.rr.recyclerally.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.rr.recyclerally.R;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.model.system.Challenge;
import com.rr.recyclerally.model.system.EItemType;
import com.rr.recyclerally.utils.DateConverter;

import java.util.Date;

public class AdminAddChallengeActivity extends AppCompatActivity {
    public static final String ADD_CHALLENGE_TAG = "AdminAddChallengeActivity";
    private FirebaseService firebaseService;
    private TextInputEditText tietItemsNumber;
    private AppCompatSpinner spnItemType;
    private TextInputEditText tietEndDate;
    private AppCompatButton btnSave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_challenge);

        initComponents();
        firebaseService = new FirebaseService();
    }

    private void initComponents() {
        tietItemsNumber = findViewById(R.id.admin_add_challenge_tiet_itemsNumber);
        initSpn();
        tietEndDate = findViewById(R.id.admin_add_challenge_tiet_end_date);
        btnSave = findViewById(R.id.admin_add_challenge_btn_save);
        btnSave.setOnClickListener(getSaveListener());
    }

    private View.OnClickListener getSaveListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    Challenge challenge = buildChallenge();
                    firebaseService.addChallenge(challenge, success -> {
                        if (success) {
                            Log.d(ADD_CHALLENGE_TAG, "Challenge saved in the database");
                            Toast.makeText(AdminAddChallengeActivity.this, "Challenge added successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(ADD_CHALLENGE_TAG, "Failed to save Challenge in the database");
                            Toast.makeText(AdminAddChallengeActivity.this, "Failed to add challenge.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }
            }
        };
    }



    //-----------------------------------------------------------------------------------
    private void initSpn() {
        spnItemType = findViewById(R.id.admin_add_challenge_spn_itemType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.item_types, android.R.layout.simple_spinner_dropdown_item);
        spnItemType.setAdapter(adapter);
    }

    private boolean isValid() {
        if (tietItemsNumber.getText() == null || Integer.parseInt(tietItemsNumber.getText().toString().trim()) > 10) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_the_maximum_number_of_recycled_items_for_a_challenge_is_10), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tietEndDate.getText() == null || DateConverter.toDate(tietEndDate.getText().toString()) == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_please_select_a_valid_date_to_end_the_challenge), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Challenge buildChallenge() {
        int itemsNumber = Integer.parseInt(tietItemsNumber.getText().toString().trim());
        String strItemType = (String) spnItemType.getSelectedItem();
        EItemType itemType = EItemType.valueOf(strItemType.toUpperCase());
        String endDate = tietEndDate.getText().toString().trim();
        return new Challenge(itemsNumber, itemType, endDate);
    }

}