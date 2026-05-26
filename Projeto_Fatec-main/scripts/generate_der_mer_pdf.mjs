import { readFileSync, writeFileSync } from "fs";
import { resolve } from "path";

const root = process.cwd();
const columnsPath = resolve(root, "docs/schema_columns.txt");
const fksPath = resolve(root, "docs/schema_fks.txt");
const outHtmlPath = resolve(root, "docs/DER_MER_FazTudoJA.html");

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

function normalizeName(name) {
  return name.toUpperCase();
}

const derParts = ["erDiagram"];
for (const tableName of tableNames) {
  derParts.push(`    ${normalizeName(tableName)} {`);
  for (const c of tables.get(tableName)) {
    const required = c.isNullable === "NO" ? "REQ" : "OPT";
    const pk = c.isPk ? "PK" : required;
    const safeType = c.dataType.replace(/\s+/g, "_");
    derParts.push(`        ${safeType} ${c.columnName} ${pk}`);
  }
  derParts.push("    }");
}

for (const fk of fks) {
  derParts.push(
    `    ${normalizeName(fk.toTable)} ||--o{ ${normalizeName(fk.fromTable)} : "${fk.fromColumn} -> ${fk.toColumn}"`,
  );
}

const derMermaid = derParts.join("\n");

const merEdges = fks
  .map((fk) => `    ${normalizeName(fk.toTable)} -->|1:N| ${normalizeName(fk.fromTable)}`)
  .filter((v, i, arr) => arr.indexOf(v) === i)
  .sort();

const merMermaid = [
  "flowchart LR",
  "    %% MER conceitual (a partir das FKs reais do banco)",
  ...tableNames.map((t) => `    ${normalizeName(t)}[${normalizeName(t)}]`),
  ...merEdges,
].join("\n");

function esc(text) {
  return text
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

const dictionaryHtml = tableNames
  .map((tableName) => {
    const rows = tables
      .get(tableName)
      .map((c) => {
        return `<tr><td>${esc(c.columnName)}</td><td>${esc(c.dataType)}</td><td>${c.isNullable === "NO" ? "Nao" : "Sim"}</td><td>${c.isPk ? "Sim" : "Nao"}</td></tr>`;
      })
      .join("");

    return `
      <section class="entity-block">
        <h3>${esc(tableName)}</h3>
        <table>
          <thead><tr><th>Coluna</th><th>Tipo</th><th>Aceita NULL</th><th>PK</th></tr></thead>
          <tbody>${rows}</tbody>
        </table>
      </section>
    `;
  })
  .join("\n");

const fkTableRows = fks
  .map((fk) => `<tr><td>${esc(fk.fkName)}</td><td>${esc(fk.fromTable)}.${esc(fk.fromColumn)}</td><td>${esc(fk.toTable)}.${esc(fk.toColumn)}</td></tr>`)
  .join("\n");

const generatedAt = new Date().toLocaleString("pt-BR");

const html = `<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>DER e MER - FazTudoJA</title>
  <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
  <script>
    mermaid.initialize({ startOnLoad: true, securityLevel: 'loose', theme: 'default' });
  </script>
  <style>
    body { font-family: Segoe UI, Arial, sans-serif; color: #1f2937; margin: 24px; }
    h1, h2, h3 { margin: 0 0 10px 0; }
    h1 { color: #0f3f87; }
    .meta { margin-bottom: 20px; color: #4b5563; }
    .card { border: 1px solid #dbe3ef; border-radius: 10px; padding: 14px; margin: 12px 0 20px; background: #f9fbff; }
    .mermaid { background: #ffffff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 8px; }
    table { width: 100%; border-collapse: collapse; margin: 8px 0 16px; }
    th, td { border: 1px solid #d1d5db; padding: 6px 8px; font-size: 12px; text-align: left; }
    th { background: #eff6ff; }
    .entity-block { page-break-inside: avoid; margin-bottom: 10px; }
  </style>
</head>
<body>
  <h1>DER e MER - Banco FazTudoJA</h1>
  <p class="meta">Gerado automaticamente em ${esc(generatedAt)} a partir do schema real do SQL Server (dbo).</p>

  <div class="card">
    <h2>MER (Modelo Entidade-Relacionamento)</h2>
    <div class="mermaid">${esc(merMermaid)}</div>
  </div>

  <div class="card">
    <h2>DER (Diagrama Entidade-Relacionamento com atributos)</h2>
    <div class="mermaid">${esc(derMermaid)}</div>
  </div>

  <div class="card">
    <h2>Relacionamentos (FKs)</h2>
    <table>
      <thead><tr><th>Constraint</th><th>Origem</th><th>Destino</th></tr></thead>
      <tbody>${fkTableRows}</tbody>
    </table>
  </div>

  <div class="card">
    <h2>Dicionario de Dados Completo</h2>
    ${dictionaryHtml}
  </div>
</body>
</html>`;

writeFileSync(outHtmlPath, html, "utf8");
console.log(outHtmlPath);
