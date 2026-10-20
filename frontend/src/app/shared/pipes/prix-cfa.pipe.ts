import { Pipe, PipeTransform } from '@angular/core';

const formateurCfa = new Intl.NumberFormat('fr-SN', {
  maximumFractionDigits: 0,
  useGrouping: true,
});

@Pipe({ name: 'prixCfa', standalone: true })
export class PrixCfaPipe implements PipeTransform {
  transform(value: number | null | undefined): string {
    if (value == null || !Number.isFinite(value)) return '—';
    const montant = formateurCfa.format(value).replace(/[\u00a0\u202f]/g, ' ');
    return `${montant} FCFA`;
  }
}
