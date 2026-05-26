import { readFileSync, writeFileSync } from "fs";
import { resolve } from "path";

const root = process.cwd();
const columnsPath = resolve(root, "docs/schema_columns.txt");
const fksPath = resolve(root, "docs/schema_fks.txt");
const outHtmlPath = resolve(root, "docs/DER_MER_ABNT_FazTudoJA.html");

const columnsRaw = readFileSync(columnsPath, "utf8").split(/\r?\n/).filter(Boolean);
const fksRaw = readFileSync(fksPath, "utf8").split(/\r?\n/).filter(Boolean);

const tables = new Map();
for (const line of columnsRaw) {
  const [tableName, columnName, dataType, isNullable, pkFlag] = line.split("|").map((v) => (v ?? "").trim());
  if (!tableName || !columnName) continue;
  if (!tables.has(tableName)) tables.set(tableName, []);
  tables.get(tableName).push({
    columnName,
    dataType: dataType || "-",
    isNullable: isNullable || "YES",
    isPk: pkFlag === "PK",
  });
}

const fks = [];
for (const line of fksRaw) {
  const [fkName, fromTable, fromColumn, toTable, toColumn] = line.split("|").map((v) => (v ?? "").trim());
  if (!fromTable || !toTable) continue;
  fks.push({ fkName, fromTable, fromColumn, toTable, toColumn });
}

const tableNames = [...tables.keys()].sort((a, b) => a.localeCompare(b));

function toUpperSnake(name) {
  return name.toUpperCase();
}

function esc(text) {
  return text
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

const derParts = ["erDiagram"];
for (const tableName of tableNames) {
  derParts.push(`    ${toUpperSnake(tableName)} {`);
  for (const c of tables.get(tableName)) {
    const required = c.isNullable === "NO" ? "REQ" : "OPT";
    const pk = c.isPk ? "PK" : required;
    const safeType = c.dataType.replace(/\s+/g, "_");
    derParts.push(`        ${safeType} ${c.columnName} ${pk}`);
  }
  derParts.push("    }");
}
for (const fk of fks) {
  derParts.push(`    ${toUpperSnake(fk.toTable)} ||--o{ ${toUpperSnake(fk.fromTable)} : "${fk.fromColumn} -> ${fk.toColumn}"`);
}
const derMermaid = derParts.join("\n");

const merEdges = fks
  .map((fk) => `    ${toUpperSnake(fk.toTable)} -->|1:N| ${toUpperSnake(fk.fromTable)}`)
  .filter((v, i, arr) => arr.indexOf(v) === i)
  .sort();

const merMermaid = [
  "flowchart LR",
  "    %% MER conceitual derivado do schema físico",
  ...tableNames.map((t) => `    ${toUpperSnake(t)}[${toUpperSnake(t)}]`),
  ...merEdges,
].join("\n");

const relationshipRows = fks
  .map((fk, idx) => `<tr><td>${idx + 1}</td><td>${esc(fk.fromTable)}.${esc(fk.fromColumn)}</td><td>${esc(fk.toTable)}.${esc(fk.toColumn)}</td><td>1:N</td></tr>`)
  .join("\n");

const dictionaryHtml = tableNames
  .map((tableName) => {
    const rows = tables
      .get(tableName)
      .map((c) => `<tr><td>${esc(c.columnName)}</td><td>${esc(c.dataType)}</td><td>${c.isNullable === "NO" ? "Nao" : "Sim"}</td><td>${c.isPk ? "Sim" : "Nao"}</td></tr>`)
      .join("\n");

    return `
      <section class="entity-block">
        <h4>${esc(tableName)}</h4>
        <table>
          <thead><tr><th>Atributo</th><th>Tipo</th><th>Nulo</th><th>PK</th></tr></thead>
          <tbody>${rows}</tbody>
        </table>
      </section>
    `;
  })
  .join("\n");

const today = new Date();
const year = today.getFullYear();
const generatedAt = today.toLocaleString("pt-BR");

const html = `<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>DER e MER - Versao ABNT - FazTudoJA</title>
  <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
  <script>
    mermaid.initialize({ startOnLoad: true, securityLevel: 'loose', theme: 'default' });
  </script>
  <style>
    @page { size: A4; margin: 3cm 2cm 2cm 3cm; }
    body { font-family: "Times New Roman", serif; font-size: 12pt; line-height: 1.5; color: #000; }
    .capa { min-height: 100vh; display: flex; flex-direction: column; justify-content: space-between; text-align: center; page-break-after: always; }
    .instituicao { margin-top: 3cm; font-weight: bold; text-transform: uppercase; }
    .titulo { margin-top: 8cm; font-weight: bold; text-transform: uppercase; }
    .subtitulo { margin-top: 0.8cm; }
    .local-data { margin-bottom: 2cm; text-transform: uppercase; }
    .sumario { page-break-after: always; }
    .sumario h2 { text-transform: uppercase; font-size: 12pt; text-align: center; margin-bottom: 1cm; }
    .sumario ol { margin: 0; padding-left: 1.2cm; }
    h1 { font-size: 12pt; margin: 0 0 0.5cm 0; text-transform: uppercase; }
    h2 { font-size: 12pt; margin: 0.8cm 0 0.4cm 0; text-transform: uppercase; }
    h3 { font-size: 12pt; margin: 0.6cm 0 0.2cm 0; }
    h4 { font-size: 12pt; margin: 0.5cm 0 0.2cm 0; text-transform: lowercase; }
    p { margin: 0 0 0.35cm 0; text-align: justify; text-indent: 1.25cm; }
    .sem-recuo { text-indent: 0; }
    .figure-caption, .table-caption { font-size: 11pt; text-align: center; margin: 0.25cm 0; font-weight: bold; }
    .mermaid-wrap { border: 1px solid #000; padding: 0.4cm; margin-bottom: 0.4cm; page-break-inside: avoid; }
    .mermaid { background: #fff; }
    table { width: 100%; border-collapse: collapse; margin-bottom: 0.5cm; page-break-inside: avoid; font-size: 10.5pt; }
    th, td { border: 1px solid #000; padding: 4px 6px; text-align: left; vertical-align: top; }
    th { font-weight: bold; }
    .small-note { font-size: 10pt; margin-top: -0.2cm; margin-bottom: 0.5cm; text-indent: 0; }
    .page-break { page-break-before: always; }
    .references p { text-indent: 0; margin-left: 0.75cm; }
  </style>
</head>
<body>
  <section class="capa">
    <div>
      <div class="instituicao">FACULDADE DE TECNOLOGIA - FATEC</div>
      <div class="instituicao">CURSO DE DESENVOLVIMENTO DE SOFTWARE MULTIPLATAFORMA</div>
    </div>
    <div>
      <div class="titulo">DER E MER DO BANCO DE DADOS</div>
      <div class="subtitulo">Projeto FazTudoJA</div>
    </div>
    <div class="local-data">SAO PAULO<br/>${year}</div>
  </section>

  <section class="sumario">
    <h2>SUMARIO</h2>
    <ol>
      <li>Introducao</li>
      <li>Metodologia de levantamento do schema</li>
      <li>Modelo Entidade-Relacionamento (MER)</li>
      <li>Diagrama Entidade-Relacionamento (DER)</li>
      <li>Dicionario de dados</li>
      <li>Relacionamentos e cardinalidades</li>
      <li>Conclusao</li>
      <li>Referencias</li>
    </ol>
  </section>

  <h1>1 Introducao</h1>
  <p>Este documento apresenta os modelos MER e DER referentes ao banco de dados do sistema FazTudoJA, com base no schema fisico atualmente implantado no SQL Server. A elaboracao segue estrutura textual e de seccionamento compativeis com apresentacao academica segundo diretrizes ABNT.</p>

  <h1>2 Metodologia de levantamento do schema</h1>
  <p>O levantamento foi realizado por consulta direta ao catalogo de metadados do SQL Server, contemplando tabelas base do schema dbo, atributos, tipos de dados, nulabilidade, chaves primarias e chaves estrangeiras.</p>
  <p class="sem-recuo"><strong>Data/hora da geracao automatica:</strong> ${esc(generatedAt)}.</p>

  <h1>3 Modelo Entidade-Relacionamento (MER)</h1>
  <div class="figure-caption">Figura 1 - MER do banco de dados FazTudoJA</div>
  <div class="mermaid-wrap"><div class="mermaid">${esc(merMermaid)}</div></div>
  <p class="small-note">Fonte: elaboracao propria com base no schema SQL Server.</p>

  <h1>4 Diagrama Entidade-Relacionamento (DER)</h1>
  <div class="figure-caption">Figura 2 - DER com entidades, atributos e relacionamentos</div>
  <div class="mermaid-wrap"><div class="mermaid">${esc(derMermaid)}</div></div>
  <p class="small-note">Fonte: elaboracao propria com base no schema SQL Server.</p>

  <div class="page-break"></div>
  <h1>5 Dicionario de dados</h1>
  <div class="table-caption">Tabela 1 - Dicionario de dados por entidade</div>
  ${dictionaryHtml}
  <p class="small-note">Fonte: catalogo de metadados do SQL Server (INFORMATION_SCHEMA).</p>

  <h1>6 Relacionamentos e cardinalidades</h1>
  <div class="table-caption">Tabela 2 - Chaves estrangeiras e cardinalidade derivada</div>
  <table>
    <thead><tr><th>#</th><th>Origem</th><th>Destino</th><th>Cardinalidade</th></tr></thead>
    <tbody>${relationshipRows}</tbody>
  </table>
  <p class="small-note">Fonte: sys.foreign_keys e sys.foreign_key_columns.</p>

  <h1>7 Conclusao</h1>
  <p>Os modelos apresentados consolidam a estrutura relacional vigente do banco de dados, servindo como artefato de apoio para manutencao, evolucao do schema e documentacao tecnica do projeto.</p>

  <h1>8 Referencias</h1>
  <section class="references">
    <p>ASSOCIACAO BRASILEIRA DE NORMAS TECNICAS. NBR 14724: informacao e documentacao - trabalhos academicos - apresentacao. Rio de Janeiro: ABNT, 2011.</p>
    <p>MICROSOFT. SQL Server Documentation. Disponivel em: https://learn.microsoft.com/sql/. Acesso em: ${today.toLocaleDateString("pt-BR")}.</p>
  </section>
</body>
</html>`;

writeFileSync(outHtmlPath, html, "utf8");
console.log(outHtmlPath);
