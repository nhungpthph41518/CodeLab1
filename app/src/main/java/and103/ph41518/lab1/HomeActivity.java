package and103.ph41518.lab1;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private EditText ed_tentp;
    private Button btnadd;
    private RecyclerView rcv;
    private adapter adapter;
    private List<model> mlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = FirebaseFirestore.getInstance();
        mlist = new ArrayList<>();
//        ghiDulieu();
        docDulieu();

        FirebaseAuth.getInstance().signOut();

        ed_tentp = findViewById(R.id.ed_tentp);
        btnadd = findViewById(R.id.btn_add);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ed_tentp.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    // Tạo dữ liệu mới
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("name", name);
                    // Thêm dữ liệu mới vào Firestore
                    db.collection("cities")
                            .add(newData)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    // Xóa dữ liệu trên TextInput
                                    ed_tentp.setText("");
                                    // Sau khi thêm thành công, cập nhật lại dữ liệu trên RecyclerView
                                    mlist.add(new model(name));
                                    // Thông báo cho adapter về sự thay đổi dữ liệu
                                    adapter.notifyDataSetChanged();
                                    // Cuộn RecyclerView xuống dòng mới (tùy chọn)
                                    rcv.smoothScrollToPosition(mlist.size() - 1);
                                    Toast.makeText(HomeActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    // Xử lý khi thêm không thành công, ví dụ: hiển thị thông báo lỗi
                                }
                            });
                } else {
                    // Xử lý trường hợp nhập liệu không hợp lệ
                    Toast.makeText(HomeActivity.this, "Vui lòng nhập tên thành phố", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rcv = findViewById(R.id.recview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcv.addItemDecoration(dividerItemDecoration);
        mlist = new ArrayList<>();
        adapter = new adapter(mlist);
        rcv.setAdapter(adapter);

    }

    //private void onclickAdduser(model model){
//    DatabasRe
//}
    private void ghiDulieu() {
        CollectionReference cities = db.collection("cities");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "San Francisco");
        data1.put("state", "CA");
        data1.put("country", "USA");
        data1.put("capital", false);
        data1.put("population", 860000);
        data1.put("regions", Arrays.asList("west_coast", "norcal"));
        cities.document("SF").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Los Angeles");
        data2.put("state", "CA");
        data2.put("country", "USA");
        data2.put("capital", false);
        data2.put("population", 3900000);
        data2.put("regions", Arrays.asList("west_coast", "socal"));
        cities.document("LA").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Washington D.C.");
        data3.put("state", null);
        data3.put("country", "USA");
        data3.put("capital", true);
        data3.put("population", 680000);
        data3.put("regions", Arrays.asList("east_coast"));
        cities.document("DC").set(data3);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "Tokyo");
        data4.put("state", null);
        data4.put("country", "Japan");
        data4.put("capital", true);data4.put("population", 9000000);
        data4.put("regions", Arrays.asList("kanto", "honshu"));
        cities.document("TOK").set(data4);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "Beijing");
        data5.put("state", null);
        data5.put("country", "China");
        data5.put("capital", true);
        data5.put("population", 21500000);
        data5.put("regions", Arrays.asList("jingjinji", "hebei"));
        cities.document("BJ").set(data5);
    }

    String TAG = "HomeActivity";

    private void docDulieu() {
        db.collection("cities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mlist.clear(); // Xóa dữ liệu cũ trước khi cập nhật
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                model model = document.toObject(model.class); // Convert document to model class
                                mlist.add(model); // Thêm model vào danh sách
                            }
                            adapter.notifyDataSetChanged(); // Cập nhật lại RecyclerView
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}