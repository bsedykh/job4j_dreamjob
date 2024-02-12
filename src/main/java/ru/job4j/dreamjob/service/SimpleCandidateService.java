package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@ThreadSafe
public class SimpleCandidateService implements CandidateService {
    private final CandidateRepository candidateRepository;
    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository sql2oCandidateRepository, FileService fileService) {
        this.candidateRepository = sql2oCandidateRepository;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto file) {
        saveNewFile(candidate, file);
        return candidateRepository.save(candidate);
    }

    private void saveNewFile(Candidate candidate, FileDto file) {
        var savedFile = fileService.save(file);
        candidate.setFileId(savedFile.getId());
    }

    @Override
    public boolean deleteById(int id) {
        var result = false;
        var candidateOptional = findById(id);
        if (candidateOptional.isPresent()) {
            candidateRepository.deleteById(id);
            fileService.deleteById(candidateOptional.get().getFileId());
            result = true;
        }
        return result;
    }

    @Override
    public boolean update(Candidate candidate, FileDto file) {
        var isNewFileEmpty = file.getContent().length == 0;
        if (isNewFileEmpty) {
            return candidateRepository.update(candidate);
        }
        var oldFileId = candidate.getFileId();
        saveNewFile(candidate, file);
        var isUpdated = candidateRepository.update(candidate);
        fileService.deleteById(oldFileId);
        return isUpdated;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}
