package com.example.pemesanantiket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pemesanantiket.R;
import com.example.pemesanantiket.model.MyTicket;
import com.example.pemesanantiket.model.Ticket;
import com.example.pemesanantiket.ui.MyTicketDetailAct;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder>{

    Context context;
    ArrayList<Ticket> myTicket;

    public TicketAdapter(Context c, ArrayList<Ticket> p){
        context = c;
        myTicket = p;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_myticket, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Ticket ticket = myTicket.get(i);
        myViewHolder.xnama_wisata.setText(ticket.getName());
        myViewHolder.xlokasi.setText(ticket.getLocation());
        myViewHolder.xjumlah_tiket.setText(ticket.getNumTicket() + " Tickets");

        final String idTicket = ticket.getIdTicket();

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotomyticketdetail = new Intent(context, MyTicketDetailAct.class);
                gotomyticketdetail.putExtra(MyTicketDetailAct.ID_TICKET_EXTRA, idTicket);
                context.startActivity(gotomyticketdetail);
            }
        });
    }

    @Override
    public int getItemCount() {

        return myTicket.size();
    }

    public void clear() {
        int size = myTicket.size();
        myTicket.clear();
        notifyItemRangeRemoved(0, size);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView xnama_wisata, xlokasi, xjumlah_tiket;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            xnama_wisata = itemView.findViewById(R.id.xnama_wisata);
            xlokasi = itemView.findViewById(R.id.xlokasi);
            xjumlah_tiket = itemView.findViewById(R.id.xjumlah_tiket);
        }
    }
}
