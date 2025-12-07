package academic.management.assistant;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.TeacherDao;
import academic.management.assistant.database.ThemeDao;
import academic.management.assistant.model.Teacher;

public class AddTeacherActivity extends AppCompatActivity {
    
    private EditText teacherNameEdit;
    private ImageView teacherImage;
    private TeacherDao teacherDao;
    private Teacher teacher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> imageCropLauncher;
    private String tempImagePath; // Store image path temporarily
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        int nightMode = themeDao.useSystemTheme() ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM :
            (themeDao.isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().setLocalNightMode(nightMode);
        
        getTheme().applyStyle(getAccentStyle(themeDao.getAccentColor()), true);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        
        teacherDao = new TeacherDao(dbHelper);
        teacherNameEdit = findViewById(R.id.teacherNameEdit);
        teacherImage = findViewById(R.id.teacherImage);
        
        teacher = new Teacher();
        
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    saveImageToApp(imageUri);
                }
            }
        );
        
        imageCropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    android.os.Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap croppedBitmap = (Bitmap) extras.get("data");
                        saveCroppedImage(croppedBitmap);
                    }
                }
            }
        );
        
        Button addImageBtn = findViewById(R.id.addImageBtn);
        Button changeImageBtn = findViewById(R.id.changeImageBtn);
        Button cropImageBtn = findViewById(R.id.cropImageBtn);
        Button removeImageBtn = findViewById(R.id.removeImageBtn);
        Button saveBtn = findViewById(R.id.saveBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);
        
        addImageBtn.post(() -> {
            int accentColor = Color.parseColor(themeDao.getAccentColor());
            GradientDrawable addBg = new GradientDrawable();
            addBg.setShape(GradientDrawable.RECTANGLE);
            addBg.setColor(accentColor);
            addBg.setCornerRadius(8 * getResources().getDisplayMetrics().density);
            addImageBtn.setBackground(addBg);
        });
        
        changeImageBtn.post(() -> {
            int accentColor = Color.parseColor(themeDao.getAccentColor());
            GradientDrawable changeBg = new GradientDrawable();
            changeBg.setShape(GradientDrawable.RECTANGLE);
            changeBg.setColor(accentColor);
            changeBg.setCornerRadius(8 * getResources().getDisplayMetrics().density);
            changeImageBtn.setBackground(changeBg);
        });
        
        cropImageBtn.post(() -> {
            int accentColor = Color.parseColor(themeDao.getAccentColor());
            GradientDrawable cropBg = new GradientDrawable();
            cropBg.setShape(GradientDrawable.RECTANGLE);
            cropBg.setColor(accentColor);
            cropBg.setCornerRadius(8 * getResources().getDisplayMetrics().density);
            cropImageBtn.setBackground(cropBg);
        });
        
        saveBtn.post(() -> {
            int accentColor = Color.parseColor(themeDao.getAccentColor());
            GradientDrawable saveBg = new GradientDrawable();
            saveBg.setShape(GradientDrawable.RECTANGLE);
            saveBg.setColor(accentColor);
            saveBg.setCornerRadius(12 * getResources().getDisplayMetrics().density);
            saveBtn.setBackground(saveBg);
        });
        
        addImageBtn.setOnClickListener(v -> openImagePicker());
        changeImageBtn.setOnClickListener(v -> openImagePicker());
        cropImageBtn.setOnClickListener(v -> cropCurrentImage());
        removeImageBtn.setOnClickListener(v -> removeImage());
        saveBtn.setOnClickListener(v -> saveTeacher());
        cancelBtn.setOnClickListener(v -> finish());
        
        updateButtonVisibility();
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    
    private void saveImageToApp(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            File imagesDir = new File(getFilesDir(), "teacher_images");
            if (!imagesDir.exists()) imagesDir.mkdirs();
            
            String fileName = "temp_teacher_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(imagesDir, fileName);
            
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();
            
            tempImagePath = imageFile.getAbsolutePath();
            teacherImage.setImageBitmap(bitmap);
            updateButtonVisibility();
            Toast.makeText(this, "Image added!", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void cropCurrentImage() {
        if (tempImagePath == null || tempImagePath.isEmpty()) {
            Toast.makeText(this, "No image to crop", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            String imageUriString = android.provider.MediaStore.Images.Media.insertImage(
                getContentResolver(), 
                tempImagePath, 
                "temp_crop", 
                null
            );
            
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(Uri.parse(imageUriString), "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            cropIntent.putExtra("return-data", true);
            
            imageCropLauncher.launch(cropIntent);
            
        } catch (Exception e) {
            Toast.makeText(this, "Crop not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveCroppedImage(Bitmap croppedBitmap) {
        try {
            File imageFile = new File(tempImagePath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();
            
            teacherImage.setImageBitmap(croppedBitmap);
            Toast.makeText(this, "Image cropped!", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save cropped image", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void removeImage() {
        new AlertDialog.Builder(this)
            .setTitle("Remove Image")
            .setMessage("Remove teacher photo?")
            .setPositiveButton("Remove", (dialog, which) -> {
                if (tempImagePath != null && !tempImagePath.isEmpty()) {
                    File imageFile = new File(tempImagePath);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }
                tempImagePath = null;
                teacherImage.setImageBitmap(null);
                teacherImage.setImageDrawable(null);
                teacherImage.setImageResource(android.R.drawable.ic_menu_camera);
                updateButtonVisibility();
                Toast.makeText(this, "Image removed!", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void updateButtonVisibility() {
        Button addImageBtn = findViewById(R.id.addImageBtn);
        Button changeImageBtn = findViewById(R.id.changeImageBtn);
        Button cropImageBtn = findViewById(R.id.cropImageBtn);
        Button removeImageBtn = findViewById(R.id.removeImageBtn);
        
        boolean hasImage = tempImagePath != null && !tempImagePath.isEmpty() && 
                          new File(tempImagePath).exists();
        
        addImageBtn.setVisibility(hasImage ? android.view.View.GONE : android.view.View.VISIBLE);
        changeImageBtn.setVisibility(hasImage ? android.view.View.VISIBLE : android.view.View.GONE);
        cropImageBtn.setVisibility(hasImage ? android.view.View.VISIBLE : android.view.View.GONE);
        removeImageBtn.setVisibility(hasImage ? android.view.View.VISIBLE : android.view.View.GONE);
    }
    
    private void saveTeacher() {
        String name = teacherNameEdit.getText().toString().trim();
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter teacher name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        teacher.fullName = name;
        long teacherId = teacherDao.insertTeacher(teacher);
        
        // If there's a temp image, rename it to final name
        if (tempImagePath != null && !tempImagePath.isEmpty()) {
            try {
                File tempFile = new File(tempImagePath);
                File finalFile = new File(tempFile.getParent(), "teacher_" + teacherId + ".jpg");
                
                if (tempFile.renameTo(finalFile)) {
                    teacher.id = (int) teacherId;
                    teacher.imagePath = finalFile.getAbsolutePath();
                    teacherDao.updateTeacher(teacher);
                }
            } catch (Exception e) {
                // Image save failed but teacher is created
            }
        }
        
        Toast.makeText(this, "Teacher added!", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private int getAccentStyle(String color) {
        switch (color) {
            case "#6200EE": return R.style.AccentPurple;
            case "#2196F3": return R.style.AccentBlue;
            case "#10B981": return R.style.AccentGreen;
            case "#F44336": return R.style.AccentRed;
            case "#FF9800": return R.style.AccentOrange;
            default: return R.style.AccentPurple;
        }
    }
}