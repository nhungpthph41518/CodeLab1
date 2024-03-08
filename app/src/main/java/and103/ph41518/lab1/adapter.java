package and103.ph41518.lab1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class adapter extends RecyclerView.Adapter<adapter.Viewholer>{
    private List<model> mlist;

    public adapter(List<model> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public Viewholer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new Viewholer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholer holder, int position) {
        model model=mlist.get(position);
        if (model==null){
            return;
        }
        holder.vtv_name.setText("Tên thành phố: "+model.getName());
    }

    @Override
    public int getItemCount() {
        if(mlist!=null){
            return mlist.size();
        }
        return 0;
    }

    public class Viewholer extends RecyclerView.ViewHolder {
        TextView vtv_name;

        public Viewholer(@NonNull View itemView) {
            super(itemView);
            vtv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
