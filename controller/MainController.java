/** @author María Fortuna */

package com.rpgmaker.controller;

import com.rpgmaker.export.JavaCodeExporter;
import com.rpgmaker.io.ProjectSerializer;
import com.rpgmaker.model.GameMap;
import com.rpgmaker.model.GameProject;

import java.io.File;
import java.io.IOException;

/**
 * Coordina el modelo del proyecto con la vista principal y sus servicios.
 */
public class MainController {
    private GameProject project = GameProject.createDefault();

    public GameProject getProject() {
        return project;
    }

    public void createProject() {
        project = GameProject.createDefault();
    }

    public void openProject(File file) throws IOException {
        project = ProjectSerializer.load(file);
    }

    public void saveProject(File file) throws IOException {
        ProjectSerializer.save(project, file);
        project.setFilePath(file.getAbsolutePath());
    }

    public void exportProject(File outputDirectory) throws IOException {
        JavaCodeExporter.export(project, outputDirectory);
    }

    public void updateProjectSettings(String name, String author, String description, String version) {
        project.setName(name);
        project.setAuthor(author);
        project.setDescription(description);
        project.setVersion(version);
    }

    public void resizeMap(GameMap map, int width, int height) {
        map.resize(width, height);
    }
}
