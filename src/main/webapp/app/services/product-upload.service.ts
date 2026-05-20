export async function uploadProductImage(productId: number, file: File) {
  const formData = new FormData();
  formData.append('file', file);

  const token = localStorage.getItem('jhi-authenticationToken') || sessionStorage.getItem('jhi-authenticationToken');

  const response = await fetch(`/api/products/${productId}/upload-image`, {
    method: 'POST',
    headers: token
      ? {
          Authorization: `Bearer ${token.replace(/"/g, '')}`,
        }
      : {},
    body: formData,
  });

  if (!response.ok) {
    throw new Error(`Erro ao enviar imagem: ${response.status}`);
  }

  return await response.json();
}
