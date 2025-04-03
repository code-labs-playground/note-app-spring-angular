package lk.ijse.dep13.springbackend.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NoteHttpController {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String createNote(){
        return "Create a new note";
    }

    @GetMapping
    public String getAllNotes(){
        return "Get all notes";
    }

    @GetMapping("/{id:^\\d+$}")
    public String getNote(@PathVariable Integer id){
        return "Get note " + id;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id:^\\d+$}}")
    public String updateNote(@PathVariable Integer id){
        return "Update note " + id;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id:^\\d+$}")
    public String deleteNote(@PathVariable Integer id){
        return "Delete note " + id;
    }
}
