package watson.ibm.wconv;

/**
 * Created by KaustavGanguli on 11/22/2017.
 */

import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import android.view.LayoutInflater;

public class Adapter extends RecyclerView.Adapter {

    private List<String[]> msg;
    private String type,message;
    private String[] element;
    private View view;

    public Adapter(List<String[]> msg) {
       this.msg=msg;
    }
    @Override
    public int getItemViewType(int position) {

        //Collect the String[] here and assign MESSAGE_TYPE & MESSAGE_TEXT
        element=msg.get(position);
        type=element[1];
        message=element[0];
        return 1;
    }
    private class SentMessageHolder extends RecyclerView.ViewHolder{

        TextView messageText_sent;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText_sent = (TextView) itemView.findViewById(R.id.text_message_body_sent);

        }

    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        public TextView messageText_received;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText_received = (TextView) itemView.findViewById(R.id.text_message_body_received);
        }

    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(type.equals("watson")){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        else if(type.equals("user")){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(type.equals("watson")){
            ReceivedMessageHolder rh=((ReceivedMessageHolder) holder);
            rh.messageText_received.setText(message);
            rh.setIsRecyclable(false);
        }
        else if(type.equals("user")){
            SentMessageHolder rh=((SentMessageHolder) holder);
            rh.messageText_sent.setText(message);
            rh.setIsRecyclable(false);
        }
    }

    public int getItemCount() {
        return msg.size();
    }
}
