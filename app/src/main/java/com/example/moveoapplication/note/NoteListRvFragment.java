package com.example.moveoapplication.note;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moveoapplication.R;
import com.example.moveoapplication.model.Model;
import com.squareup.picasso.Picasso;


public class NoteListRvFragment extends Fragment {

    View view;
    String user_email;
    TextView listSizeTv;
    NoteAdapter adapter;
    NoteViewModel viewModel;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_note_list_rv, container, false);


        user_email = NoteListRvFragmentArgs.fromBundle(getArguments()).getUserEmail();
        listSizeTv = view.findViewById(R.id.noteslist_count);

        viewModel.getNotesData().observe(getViewLifecycleOwner(), list1 -> refresh());

        swipeRefresh = view.findViewById(R.id.noteslist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            Model.instance.refreshUserNotesList(user_email);
        });

        swipeRefresh.setRefreshing(Model.instance.getNotesListLoadingState().getValue() == Model.NotesListLoadingState.loading);

        Model.instance.getNotesListLoadingState().observe(getViewLifecycleOwner(), postsListLoadingState -> {
            if (postsListLoadingState == Model.NotesListLoadingState.loading) {
                swipeRefresh.setRefreshing(true);
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        // rv :

        RecyclerView list = view.findViewById(R.id.notesList_rv);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NoteAdapter();
        list.setHasFixedSize(true);
        list.setAdapter(adapter);


        adapter.setOnItemClickListener((v, position) -> {

            String nId = String.valueOf(viewModel.getNotesData().getValue().get(position).getId());

            Log.d("TAG", "row was clicked " + position);
            Navigation.findNavController(view).navigate(NoteListRvFragmentDirections.actionNoteListRvFragmentToEditNoteFragment(nId));
        });

        return view;

    }

    private void refresh() {
        if (adapter.getItemCount() == 0) {
            listSizeTv.setText("You don't have any notes yet");
        }
        adapter.notifyDataSetChanged();
    }


    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }


    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView desc_tv;
        ImageView note_imv;
        TextView title_tv;
        TextView date_tv;


        public NoteViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            note_imv = itemView.findViewById(R.id.note_row_imv);
            desc_tv = itemView.findViewById(R.id.note_row_des_tv);
            title_tv = itemView.findViewById(R.id.note_row_title_tv);
            date_tv = itemView.findViewById(R.id.note_row_date_tv);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }

        void bind(Note note) {

            note_imv.setImageResource(R.drawable.notepicture);
            desc_tv.setText(note.getDescription());
            title_tv.setText(note.getTitle());
            date_tv.setText(note.getDate());

            if (note.getImageUrl() != null) {
                Picasso.get()
                        .load(note.getImageUrl())
                        .into(note_imv);
            }
        }
    }


    class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

        OnItemClickListener listener;

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.note_row, parent, false);
            NoteViewHolder holder = new NoteViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            Note note = viewModel.getNotesData().getValue().get(position);
            holder.bind(note);
        }

        @Override
        public int getItemCount() {
            if (viewModel.getNotesData().getValue() == null) {
                return 0;
            }
            return viewModel.getNotesData().getValue().size();
        }
    }

    /* **************************************** Menu ************************************************ */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.feed_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.menu_addNote:
                    Navigation.findNavController(this.view).navigate(NoteListRvFragmentDirections.actionNoteListRvFragmentToMapsFragment(true));
                    break;

            }
        } else {
            return true;
        }
        return false;
    }

}