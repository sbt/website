import { downloadUrl, VersionList } from '@site/variables';

function VersionRow({v}) {
  return (
    <tr>
      <td>sbt {v}</td>
      <td><a href={ downloadUrl(v, v, ".zip") }>.zip</a></td>
      <td><a href={ downloadUrl(v, v, ".zip.sha256") }>.zip.sha256</a></td>
      <td><a href={ downloadUrl(v, v, ".tgz") }>.tgz</a></td>
      <td><a href={ downloadUrl(v, v, ".tgz.sha256") }>.tgz.sha256</a></td>
      <td><a href={ downloadUrl(v, v, ".msi") }>.msi</a></td>
      <td><a href={ downloadUrl(v, v, ".msi.sha256") }>.msi.sha256</a></td>
    </tr>
  );
}

export default function HomepageVersions() {
  return (
    <table>
      <tr>
        <th>sbt version</th>
        <th>.zip</th>
        <th>.zip.sha256</th>
        <th>.tgz</th>
        <th>.tgz.sha256</th>
        <th>.msi</th>
        <th>.msi.sha256</th>
      </tr>
      {VersionList.map((v, idx) => (
        <VersionRow key={idx} {...v} />
      ))}
    </table>
  );
}
