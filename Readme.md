# ADS04 Android

## 수업 내용
- Content Resolver를 활용한 주소록 불러오는 예제 학습

## Code Review

### BaseActivity

```Java
public abstract class BaseActivity extends AppCompatActivity {

    private static final int REQ_CODE = 999;
    private static final String permissions[] = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
    };

    public abstract void init();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 앱 버전 체크 - 호환성 처리
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkPermission();
        }else{
            init();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(){
        // 권한에 대한 승인 여부
        List<String> requires = new ArrayList<>();
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                requires.add(permission);
            }
        }
        // 승인이 안된 권한이 있을 경우만 승인 요청
        if(requires.size() > 0){
            String perms[] = requires.toArray(new String[requires.size()]);
            requestPermissions(perms, REQ_CODE);
        }else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 권한 승인 여부 체크
        if(requestCode == REQ_CODE){
            boolean granted = true;
            for(int grant : grantResults){
                if(grant != PackageManager.PERMISSION_GRANTED){
                    granted = false;
                    break;
                }
            }
            if(granted){
                init();
            }
        }
    }
}
```

### MainActivity

```Java
public class MainActivity extends BaseActivity { // BaseActivity를 상속받음.
        RecyclerView recyclerView;
        CustomAdapter adapter;
        @Override
        public void init() { // BaseActivity에서 추상클래스로 정의한 것을 오버라이드해서 받아옴.
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
```

- 편의상 모델 클래스를 메인에 넣어서 학습.

### CustomAdapter

``` Java
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Holder>{
    List<Contact> data;

    public void setDataAndRefresh(List<Contact> data){
        this.data = data;
        // 데이터가 변경되었음을 알린다
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Contact contact = data.get(position);
        holder.setNumber(contact.getNumber()); 
        holder.setTextName(contact.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        private String number;
        private TextView textNumber;
        private TextView textName;
        private ImageButton btnCall;
        public Holder(View v) {
            super(v);
            textNumber = (TextView) v.findViewById(R.id.textNumber);
            textName = (TextView) v.findViewById(R.id.textName);
            btnCall = (ImageButton) v.findViewById(R.id.btnCall);
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String num = "tel:" + number;
                    Uri uri = Uri.parse(num);
                    Intent intent = new Intent("android.intent.action.CALL",uri);
                    view.getContext().startActivity(intent);
                }
            });
        }
        
        // 이런식으로 홀더안에 메소드를 정의해서 사용할 수 있음.
        public void setNumber(String number){ // 쿼리로 받아온 번호를 홀더에 있는 textView에 세팅.
            this.number = number;
            textNumber.setText(this.number);
        }
        public void setTextName(String name){
            textName.setText(name);
        }
    }
}

```



## 보충설명

![Content Provider-Resolver](http://cfile5.uf.tistory.com/image/1178C5014AFDAE6A083D75)

### ContentProvider과 ContentResolver

#### ContentProvider 개념 및 특징

- 컨텐트 프로바이더는 어플리케이션 내의 데이터베이스를 다른 어플리케이션이 사용할 수 있는 "통로"를 제공
- 이 과정에서 컨텐트 프로바이더를 통해 외부 어플리케이션이 접근할 수 있는 범위를 정해줄 수 있어, "공유할 것만 공유하는" 것이 가능
- 컨텐트 프로바이더를 사용하여 안드로이드 시스템의 각종 설정값이나 SD카드 내의 미디어 등에 접근하는 것이 가능
- 컨텐트 프로바이더에 접근하기 위해서는 해당 컨텐트 프로바이더의 주소가 필요 

#### ContentResolver 개념 및 특징

- 컨텐트 프로바이더에 접근할 때는 컨텐트 프로바이더의 주소와 컨텐트 리졸버(Content Resolver)가 필요
- 컨텐트 리졸버는 컨텐트 프로바이더의 주소를 통해 해당 컨텐트 프로바이더에 접근하여 컨텐트 프로바이더의 데이터에 접근할 수 있도록 해주는 역할
- 컨텐트 리졸버는 액티비티 클래스 내의 getContentResolver()메소드를 통해 인스턴스를 받아올 수 있음
- 일단 컨텐트 리졸버의 인스턴스를 받아온 후에는 query, insert 등의 메소드을 통해 데이터를 받거나 입력, 수정하고 싶은 컨텐트 프로바이더의 URI(Uniform Resource Identifier)를 넘겨주면 해당 컨텐트 프로바이더에 접근하여 요청한 작업을 수행할 수 있음 


#### 컨텐트 프로바이더의 주소(URI)는 일반적으로 아래와 같은 모습

![uri](http://cfile8.uf.tistory.com/image/1519620F4B62813329E103)
1. 컨텐트 프로바이더에 의해 제공되는 데이터임을 알립니다. 이 부분은 변하지 않습니다.
2. 컨텐트 프로바이더의 authority부분입니다. 각 컨텐트 프로바이더의 고유 이름입니다.
3. 컨텐트 프로바이더의 Path 부분이며, 어떤 데이터를 반환할지를 이 부분을 통해 지정합니다. 
4. 3번 부분의 Path 하위의 데이터 중 하나를 가리키는 것으로, 해당 데이터의 ID를 나타냅니다.

### 출처

- 출처: http://androidhuman.com/279 [커니의 안드로이드 이야기]


## TODO

- content resolver를 통해 가지고 온 어플리케이션 내의 데이터를 원하는 흐름으로 가공하고 만드는 연습

## Retrospect

- Content Provider를 더 깊게 파보면 여러 안드로이드의 내부 적인 흐름? 형태?등을 알 수 있을 것 같다.

## Output
- 생략