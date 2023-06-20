import { useSession } from 'next-auth/react';
import { api } from '~/utils/api';

export function useApps() {
  const { data: sessionData } = useSession();
  const { data: data } = api.apps.list.useQuery(
    { pageSize: 25 }, // no input
    { enabled: sessionData?.user !== undefined }
  );

  return data ?? [];
}
