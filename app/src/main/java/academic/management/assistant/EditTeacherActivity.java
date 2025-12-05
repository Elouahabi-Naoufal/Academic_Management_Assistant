package academic.management.assistant;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import academic.management.assistant.database.ClassDao;
import academic.management.assistant.database.DatabaseHelper;
import academic.management.assistant.database.TeacherDao;
import academic.management.assistant.database.ThemeDao;
import academic.management.assistant.model.Teacher;
import academic.management.assistant.model.ClassItem;
import java.util.List;

public class EditTeacherActivity extends AppCompatActivity {
    
    private EditText teacherNameEdit;
    private ImageView teacherImage;
    private LinearLayout classesContainer;
    private TeacherDao teacherDao;
    private ClassDao classDao;
    private Teacher teacher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> imageCropLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ThemeDao themeDao = new ThemeDao(dbHelper);
        
        int nightMode = themeDao.isDarkTheme() ? 
            AppCompatDelegate.MODE_NIGHT_YES : 
            AppCompatDelegate.MODE_NIGHT_NO;
        getDelegate().setLocalNightMode(nightMode);
        
        getTheme().applyStyle(getAccentStyle(themeDao.getAccentColor()), true);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher);
        
        teacherDao = new TeacherDao(dbHelper);
        classDao = new ClassDao(dbHelper);
        
        int teacherId = getIntent().getIntExtra("TEACHER_ID", -1);
        teacher = teacherDao.getTeacherById(teacherId);
        
        if (teacher == null) {
            finish();
            return;
        }
        
        teacherNameEdit = findViewById(R.id.teacherNameEdit);
        teacherImage = findViewById(R.id.teacherImage);
        classesContainer = findViewById(R.id.classesContainer);
        
        teacherNameEdit.setText(teacher.fullName);
        loadTeacherImage();
        loadClasses();
        
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
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap croppedBitmap = (Bitmap) extras.get("data");
                        saveCroppedImage(croppedBitmap);
                    }
                }
            }
        );
        
        Button addImageBtn = findViewById(R.id.addImageBtn);
        Button changeImageBtn = findViewById(R.id.changeImageBtn);
        Button removeImageBtn = findViewById(R.id.removeImageBtn);
        
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
        
        addImageBtn.setOnClickListener(v -> openImagePicker());
        changeImageBtn.setOnClickListener(v -> showImageOptions());
        removeImageBtn.setOnClickListener(v -> removeImage());
        
        updateButtonVisibility();
        
        Button saveBtn = findViewById(R.id.saveBtn);
        Button deleteBtn = findViewById(R.id.deleteBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);
        
        saveBtn.post(() -> {
            int accentColor = Color.parseColor(themeDao.getAccentColor());
            GradientDrawable saveBg = new GradientDrawable();
            saveBg.setShape(GradientDrawable.RECTANGLE);
            saveBg.setColor(accentColor);
            saveBg.setCornerRadius(12 * getResources().getDisplayMetrics().density);
            saveBtn.setBackground(saveBg);
        });
        
        saveBtn.setOnClickListener(v -> saveTeacher());
        deleteBtn.setOnClickListener(v -> showDeleteDialog());
        cancelBtn.setOnClickListener(v -> finish());
    }
    
    private void loadClasses() {
        List<ClassItem> classes = classDao.getClassesByTeacher(teacher.id);
        classesContainer.removeAllViews();
        
        if (classes.isEmpty()) {
            TextView noClasses = new TextView(this);
            noClasses.setText("No classes assigned to this teacher");
            noClasses.setTextSize(16);
            noClasses.setTextColor(Color.parseColor("#64748B"));
            noClasses.setPadding(0, 8, 0, 8);
            classesContainer.addView(noClasses);
        } else {
            for (ClassItem classItem : classes) {
                TextView classView = new TextView(this);
                classView.setText("• " + classItem.title + " (" + classItem.getWeekdayName() + " " + classItem.startTime + ")");
                classView.setTextSize(16);
                classView.setTextColor(Color.parseColor("#64748B"));
                classView.setPadding(0, 8, 0, 8);
                classesContainer.addView(classView);
            }
        }
    }
    
    private void showImageOptions() {
        new AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(new String[]{"Choose from Gallery", "Crop Current Image"}, (dialog, which) -> {
                if (which == 0) {
                    openImagePicker();
                } else {
                    cropCurrentImage();
                }
            })
            .show();
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    
    private void cropCurrentImage() {
        if (teacher.imagePath == null || teacher.imagePath.isEmpty()) {
            Toast.makeText(this, "No image to crop", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            File imageFile = new File(teacher.imagePath);
            String imageUriString = android.provider.MediaStore.Images.Media.insertImage(
                getContentResolver(), 
                teacher.imagePath, 
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
            File imagesDir = new File(getFilesDir(), "teacher_images");
            if (!imagesDir.exists()) imagesDir.mkdirs();
            
            String fileName = "teacher_" + teacher.id + ".jpg";
            File imageFile = new File(imagesDir, fileName);
            
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();
            
            teacher.imagePath = imageFile.getAbsolutePath();
            teacherDao.updateTeacher(teacher);
            loadTeacherImage();
            
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
                if (teacher.imagePath != null && !teacher.imagePath.isEmpty()) {
                    File imageFile = new File(teacher.imagePath);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }
                teacher.imagePath = null;
                teacherDao.updateTeacher(teacher);
                teacherImage.setImageResource(android.R.drawable.ic_menu_camera);
                updateButtonVisibility();
                Toast.makeText(this, "Image removed!", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void saveImageToApp(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            File imagesDir = new File(getFilesDir(), "teacher_images");
            if (!imagesDir.exists()) imagesDir.mkdirs();
            
            String fileName = "teacher_" + teacher.id + ".jpg";
            File imageFile = new File(imagesDir, fileName);
            
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();
            
            teacher.imagePath = imageFile.getAbsolutePath();
            teacherDao.updateTeacher(teacher);
            loadTeacherImage();
            
            Toast.makeText(this, "Image added! Use Change → Crop to adjust it", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadTeacherImage() {
        if (teacher.imagePath != null && !teacher.imagePath.isEmpty()) {
            File imageFile = new File(teacher.imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(teacher.imagePath);
                teacherImage.setImageBitmap(bitmap);
            }
        }
        updateButtonVisibility();
    }
    
    private void updateButtonVisibility() {
        Button addImageBtn = findViewById(R.id.addImageBtn);
        Button changeImageBtn = findViewById(R.id.changeImageBtn);
        Button removeImageBtn = findViewById(R.id.removeImageBtn);
        
        boolean hasImage = teacher.imagePath != null && !teacher.imagePath.isEmpty() && 
                          new File(teacher.imagePath).exists();
        
        addImageBtn.setVisibility(hasImage ? android.view.View.GONE : android.view.View.VISIBLE);
        changeImageBtn.setVisibility(hasImage ? android.view.View.VISIBLE : android.view.View.GONE);
        removeImageBtn.setVisibility(hasImage ? android.view.View.VISIBLE : android.view.View.GONE);
    }
    
    private void saveTeacher() {
        String name = teacherNameEdit.getText().toString().trim();
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter teacher name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        teacher.fullName = name;
        teacherDao.updateTeacher(teacher);
        Toast.makeText(this, "Teacher updated!", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void showDeleteDialog() {
        List<ClassItem> classes = classDao.getClassesByTeacher(teacher.id);
        if (!classes.isEmpty()) {
            StringBuilder message = new StringBuilder("Cannot delete teacher with assigned classes:\n\n");
            for (ClassItem classItem : classes) {
                message.append("• ").append(classItem.title).append("\n");
            }
            message.append("\nPlease delete or reassign these classes first.");
            
            new android.app.AlertDialog.Builder(this)
                .setTitle("Cannot Delete Teacher")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
            return;
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Delete Teacher")
                .setMessage("Delete " + teacher.fullName + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    teacherDao.deleteTeacher(teacher.id);
                    Toast.makeText(this, "Teacher deleted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
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