const fs = require('fs');

const files = [
  'src/pages/prestador/servicos-disponiveis.tsx',
  'src/pages/prestador/dashboard-prestador.tsx',
  'src/components/dashboard-prestador-layout.tsx'
];

files.forEach(filepath => {
  try {
    let content = fs.readFileSync(filepath, 'utf-8');
    // Remove all backslash followed by any character
    content = content.replace(/\\(.)/g, '$1');
    fs.writeFileSync(filepath, content);
    console.log(`Fixed: ${filepath}`);
  } catch (e) {
    console.log(`Error fixing ${filepath}: ${e.message}`);
  }
});
