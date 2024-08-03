package br.ufc.quixada.usoroomdatabase;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import br.ufc.quixada.usoroomdatabase.database.AppDatabase;
import br.ufc.quixada.usoroomdatabase.models.Pessoa;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private PessoaAdapter pessoaAdapter;
    private RecyclerView recyclerView;
    private EditText nomeEditText, cursoEditText, idadeEditText;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        nomeEditText = findViewById(R.id.nomeEditText);
        cursoEditText = findViewById(R.id.cursoEditText);
        idadeEditText = findViewById(R.id.idadeEditText);
        addButton = findViewById(R.id.addButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "pessoa-database").allowMainThreadQueries().build();

        List<Pessoa> pessoas = db.pessoaDao().getAllPessoas();
        pessoaAdapter = new PessoaAdapter(pessoas);
        recyclerView.setAdapter(pessoaAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Pessoa pessoaToRemove = pessoaAdapter.getPessoaAt(position);

                db.pessoaDao().delete(pessoaToRemove);
                pessoaAdapter.removePessoa(position);
            }
        }).attachToRecyclerView(recyclerView);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = nomeEditText.getText().toString();
                String curso = cursoEditText.getText().toString();
                String idadeStr = idadeEditText.getText().toString();

                if (!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(curso) && !TextUtils.isEmpty(idadeStr)) {
                    int idade = Integer.parseInt(idadeStr);
                    Pessoa novaPessoa = new Pessoa(nome, curso, idade);

                    db.pessoaDao().insertAll(novaPessoa);

                    pessoaAdapter.addPessoa(novaPessoa);

                    nomeEditText.setText("");
                    cursoEditText.setText("");
                    idadeEditText.setText("");
                }
            }
        });
    }
}
