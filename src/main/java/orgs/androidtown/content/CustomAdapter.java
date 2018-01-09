package orgs.androidtown.content;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jisang on 2017-09-26.
 */

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

