const fs = require('fs');
const path = require('path');

// Configuration
const CONFIG = {
    outputFile: 'project-context.txt',
    maxFileSize: 500000, // 500KB max par fichier

    // Extensions de fichiers à inclure
    includeExtensions: [
        '.java', '.xml', '.properties', '.yml', '.yaml',
        '.sql', '.md', '.gradle', '.txt', '.json'
    ],

    // Dossiers à exclure
    excludeDirs: [
        'target', 'node_modules', '.git', '.idea',
        'build', 'out', '.gradle', '.mvn'
    ],

    // Fichiers spécifiques à toujours inclure
    alwaysInclude: [
        'pom.xml', 'build.gradle', 'settings.gradle',
        'README.md', 'HELP.md', '.gitignore',
        'application.properties', 'application.yml',
        'application-dev.properties', 'application-prod.properties'
    ]
};

class ProjectContextGenerator {
    constructor() {
        this.output = [];
        this.fileCount = 0;
        this.totalSize = 0;
    }

    shouldIncludeFile(filePath, fileName) {
        // Toujours inclure les fichiers spécifiques
        if (CONFIG.alwaysInclude.includes(fileName)) {
            return true;
        }

        // Vérifier l'extension
        const ext = path.extname(fileName).toLowerCase();
        return CONFIG.includeExtensions.includes(ext);
    }

    shouldExcludeDir(dirName) {
        return CONFIG.excludeDirs.some(excluded =>
            dirName.toLowerCase().includes(excluded.toLowerCase())
        );
    }

    addSection(title, content = '') {
        this.output.push('\n' + '='.repeat(80));
        this.output.push(title);
        this.output.push('='.repeat(80));
        if (content) {
            this.output.push(content);
        }
        this.output.push('');
    }

    addFileContent(filePath, relativePath) {
        try {
            const stats = fs.statSync(filePath);

            // Ignorer les fichiers trop volumineux
            if (stats.size > CONFIG.maxFileSize) {
                console.log(`⚠️  Fichier trop volumineux ignoré: ${relativePath} (${this.formatSize(stats.size)})`);
                return;
            }

            const content = fs.readFileSync(filePath, 'utf8');

            this.output.push(`\n${'─'.repeat(80)}`);
            this.output.push(`FICHIER: ${relativePath}`);
            this.output.push(`TAILLE: ${this.formatSize(stats.size)}`);
            this.output.push('─'.repeat(80));
            this.output.push(content);
            this.output.push('');

            this.fileCount++;
            this.totalSize += stats.size;

        } catch (error) {
            console.error(`❌ Erreur lecture ${relativePath}: ${error.message}`);
        }
    }

    formatSize(bytes) {
        if (bytes < 1024) return bytes + ' B';
        if (bytes < 1048576) return (bytes / 1024).toFixed(2) + ' KB';
        return (bytes / 1048576).toFixed(2) + ' MB';
    }

    generateTree(dir, prefix = '', relativePath = '') {
        let tree = [];

        try {
            const items = fs.readdirSync(dir);

            items.forEach((item, index) => {
                const fullPath = path.join(dir, item);
                const itemRelativePath = path.join(relativePath, item);
                const isLast = index === items.length - 1;
                const stats = fs.statSync(fullPath);

                const connector = isLast ? '└── ' : '├── ';
                const extension = prefix + connector;

                if (stats.isDirectory()) {
                    if (!this.shouldExcludeDir(item)) {
                        tree.push(extension + item + '/');
                        const childPrefix = prefix + (isLast ? '    ' : '│   ');
                        tree = tree.concat(this.generateTree(fullPath, childPrefix, itemRelativePath));
                    }
                } else {
                    tree.push(extension + item);
                }
            });
        } catch (error) {
            console.error(`❌ Erreur lecture dossier ${dir}: ${error.message}`);
        }

        return tree;
    }

    scanDirectory(dir, relativePath = '') {
        try {
            const items = fs.readdirSync(dir);

            items.forEach(item => {
                const fullPath = path.join(dir, item);
                const itemRelativePath = path.join(relativePath, item);
                const stats = fs.statSync(fullPath);

                if (stats.isDirectory()) {
                    if (!this.shouldExcludeDir(item)) {
                        this.scanDirectory(fullPath, itemRelativePath);
                    }
                } else {
                    if (this.shouldIncludeFile(fullPath, item)) {
                        this.addFileContent(fullPath, itemRelativePath);
                    }
                }
            });
        } catch (error) {
            console.error(`❌ Erreur scan ${dir}: ${error.message}`);
        }
    }

    generate(projectRoot = '.') {
        console.log('🚀 Génération du contexte du projet...\n');

        const projectName = path.basename(path.resolve(projectRoot));
        const timestamp = new Date().toISOString();

        // En-tête
        this.addSection('CONTEXTE DU PROJET',
            `Projet: ${projectName}\n` +
            `Date de génération: ${timestamp}\n` +
            `Chemin: ${path.resolve(projectRoot)}`
        );

        // Arborescence
        this.addSection('STRUCTURE DU PROJET');
        const tree = this.generateTree(projectRoot);
        this.output.push(projectName + '/');
        this.output = this.output.concat(tree);

        // Contenu des fichiers
        this.addSection('CONTENU DES FICHIERS');
        this.scanDirectory(projectRoot);

        // Statistiques
        this.addSection('STATISTIQUES',
            `Nombre de fichiers analysés: ${this.fileCount}\n` +
            `Taille totale: ${this.formatSize(this.totalSize)}`
        );

        // Écriture du fichier
        const outputPath = path.join(projectRoot, CONFIG.outputFile);
        fs.writeFileSync(outputPath, this.output.join('\n'), 'utf8');

        console.log('\n✅ Contexte généré avec succès!');
        console.log(`📁 Fichier: ${outputPath}`);
        console.log(`📊 Fichiers analysés: ${this.fileCount}`);
        console.log(`💾 Taille totale: ${this.formatSize(this.totalSize)}`);
        console.log(`📄 Taille du fichier de sortie: ${this.formatSize(fs.statSync(outputPath).size)}`);
    }
}

// Exécution
if (require.main === module) {
    const projectRoot = process.argv[2] || '.';
    const generator = new ProjectContextGenerator();
    generator.generate(projectRoot);
}

module.exports = ProjectContextGenerator;