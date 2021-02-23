package adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noxnotes.MainActivity;
import com.example.noxnotes.R;
import com.example.noxnotes.ShowNoteActivity;

import java.util.List;

import interfaces.NoteStateChangeListener;
import objects.NoteObject;
import app.*;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    Activity activity ;
    List<NoteObject> list ;

    public NoteAdapter (Activity activity , List<NoteObject> list){

        this.activity = activity ;
        this.list = list ;

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_note_row , parent , false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {


        holder.title.setText(list.get(position).getTitle());
        holder.message.setText(list.get(position).getMessage());
        holder.icon.setImageDrawable(app.getImage(list.get(position)));
        holder.state.setImageResource(
                list.get(position).getSeen() == 1 ? R.drawable.ic_checked : R.drawable.ic_circle
        );

        holder.title.setTextColor(spref.getOurInstance().getInt(ROUTER.TEXT_COLOR   , Color.BLACK));
        holder.message.setTextColor(spref.getOurInstance().getInt(ROUTER.TEXT_COLOR , Color.BLACK));
        holder.title.setTextSize(spref.getOurInstance().getInt(ROUTER.FONT_SIZE , 16));
        holder.message.setTextSize(spref.getOurInstance().getInt(ROUTER.FONT_SIZE , 16));
        holder.title.setTypeface(MainActivity.font);
        holder.message.setTypeface(MainActivity.font);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatImageView icon , state ;
        AppCompatTextView title , message ;
        RelativeLayout parent ;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            state = itemView.findViewById(R.id.state);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);
            parent = itemView.findViewById(R.id.parent);


            parent.setOnClickListener(this);
            state.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v == parent){

                Intent intent = new Intent(activity , ShowNoteActivity.class);
                intent.putExtra("object" , list.get(getAdapterPosition()));
                activity.startActivity(intent);

            }else {

                app.changeNoteState(0 , list.get(getAdapterPosition()), new NoteStateChangeListener() {
                    @Override
                    public void onChange(int position, int noteID, int state , Boolean success) {

                        NoteObject noteObject = list.get(getAdapterPosition());
                        if (success){

                            noteObject.setSeen(state);
                           // NoteBroadCast.send(position , NoteBroadCast.ACTION_EDIT , noteObject);
                            list.set(getAdapterPosition() , noteObject);
                            NoteViewHolder.this.state.setImageResource(
                                    noteObject.getSeen()==1?R.drawable.ic_checked:R.drawable.ic_circle
                            );


                        }
                   
                    }

                    @Override
                    public void onStart() {
                        //progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                       // progressBar.setVisibility(View.INVISIBLE);
                    }


                });

            }
        }
    }
}
