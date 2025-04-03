package lk.ijse.dep13.springbackend.api;

import lk.ijse.dep13.springbackend.entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteHttpController {

    @Autowired
    private Connection connection;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Note createNote(@SessionAttribute("user") String email, @RequestBody Note note) throws SQLException {
        if (email == null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "This operation only supports for authorized users");
        try(var stm = connection.prepareStatement("INSERT INTO note (text, \"user\", color) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, note.getText());
            stm.setString(2, email);
            stm.setString(3, note.getColor());
            stm.executeUpdate();
            ResultSet rst = stm.getGeneratedKeys();
            rst.next();
            int id = rst.getInt(1);
            note.setId(id);
            return note;
        }
    }

    @GetMapping
    public List<Note> getAllNotes(@SessionAttribute("user")  String email) throws SQLException {
        if (email == null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "This operation only supports for authorized users");
        try(var stm = connection.prepareStatement("SELECT * FROM note WHERE \"user\" = ?")) {
            // When Considering the Performance, we can use LinkedList for these kinds of Operations
            LinkedList<Note> noteList = new LinkedList<>();
            stm.setString(1, email);
            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                int id = rst.getInt("id");
                String text = rst.getString("text");
                String color = rst.getString("color");
                noteList.add(new Note(id,text,color));
            }
            return noteList;
        }
    }

    @GetMapping("/{id:^\\d+$}")
    public Note getNote(@PathVariable Integer id, @SessionAttribute("user") String email) throws SQLException {
        if (email == null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "This operation only supports for authorized users");
        try(var stm = connection.prepareStatement("SELECT * FROM note WHERE id = ? AND \"user\" = ?")) {
            stm.setInt(1, id);
            stm.setString(2, email);
            ResultSet rst = stm.executeQuery();
            if (!rst.next()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            String text = rst.getString("text");
            String color = rst.getString("color");
            return new Note(id,text,color);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id:^\\d+$}")
    public String updateNote(@PathVariable Integer id){
        return "Update note " + id;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id:^\\d+$}")
    public void deleteNote(@PathVariable Integer id, @SessionAttribute("user") String email) throws SQLException {
        if (email == null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "This operation only supports for authorized users");
        try(var stm = connection.prepareStatement("DELETE FROM note WHERE id = ? AND \"user\" = ?")) {
            stm.setInt(1, id);
            stm.setString(2, email);
            stm.executeUpdate();
        }
    }
}
