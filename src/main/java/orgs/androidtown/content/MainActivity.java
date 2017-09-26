package orgs.androidtown.content;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


    public class MainActivity extends BaseActivity {
        RecyclerView recyclerView;
        CustomAdapter adapter;
        @Override
        public void init() {
            setContentView(R.layout.activity_main);
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            adapter = new CustomAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            List<Contact> data = load();
            adapter.setDataAndRefresh(data);
        }


        private List<Contact> load(){
            List<Contact> contacts = new ArrayList<>();
            // 1. Content Resolver 불러오기
            ContentResolver  resolver = getContentResolver();
            // 2. 데이터 URI 정의
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            // 3. 가져올 컬럼 정의
            String projection[] = {
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            // 4. 쿼리 결과 -> Cursor
            Cursor cursor = resolver.query(uri, projection, null, null, null);
            // 5. cursor 반복처리
            if(cursor != null){
                while(cursor.moveToNext()){
                    Contact contact = new Contact();
                    int index = cursor.getColumnIndex(projection[1]);
                    contact.setName(cursor.getString(index));
                    index = cursor.getColumnIndex(projection[2]);
                    contact.setNumber(cursor.getString(index));
                    index = cursor.getColumnIndex(projection[0]);
                    contact.setId(cursor.getInt(index));
                    contacts.add(contact);
                }
            }
            return contacts;
        }
    }

    class Contact {
        private int id;
        private String name;
        private String number;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }